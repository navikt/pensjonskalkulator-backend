package no.nav.pensjon.kalkulator.opptjening.client.popp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.map.OpptjeningsgrunnlagMapper
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration
import java.util.*

/**
 * Client for accessing the 'popp' service (see https://github.com/navikt/popp)
 */
@Component
class PoppOpptjeningClient(
    @Value("\${popp.url}") private val baseUrl: String,
    private val webClient: WebClient,
    private val callIdGenerator: CallIdGenerator,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : OpptjeningsgrunnlagClient, Pingable {
    private val log = KotlinLogging.logger {}

    /**
     * Calls PROPOPP007
     */
    override fun fetchOpptjeningsgrunnlag(pid: Pid): Opptjeningsgrunnlag {
        log.debug { "GET from URI: '${displayableUri(pid)}'" }

        try {
            val response = webClient
                .get()
                .uri(uri(pid))
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(OpptjeningsgrunnlagResponseDto::class.java)
                .retryWhen(retryBackoffSpec(displayableUri(pid)))
                .block()
                ?: emptyDto()

            return OpptjeningsgrunnlagMapper.fromDto(response)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val uri = baseUrl + PING_PATH

        try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .headers(::setPingHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""

            return PingResult(service, ServiceStatus.UP, uri, responseBody)
        } catch (e: WebClientResponseException) {
            return PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        } catch (e: RuntimeException) { // e.g. when connection broken
            return PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "Ping failed")
        }
    }

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = callIdGenerator.newId()
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = callIdGenerator.newId()
    }

    private fun uri(pid: Pid) = "$baseUrl$OPPTJENINGSGRUNNLAG_PATH/${pid.value}"

    private fun displayableUri(pid: Pid) = "$baseUrl$OPPTJENINGSGRUNNLAG_PATH/${pid.displayValue}"

    private fun retryBackoffSpec(uri: String): RetryBackoffSpec =
        Retry.backoff(retryAttempts.toLong(), Duration.ofSeconds(1))
            .filter { it is EgressException && !it.isClientError }
            .onRetryExhaustedThrow { backoff, signal -> handleFailure(backoff, signal, uri) }

    private fun handleFailure(backoff: RetryBackoffSpec, retrySignal: Retry.RetrySignal, uri: String): Throwable {
        log.info { "Retried calling $uri ${backoff.maxAttempts} times" }

        return when (val failure = retrySignal.failure()) {
            is WebClientRequestException -> EgressException(true, "Failed calling ${failure.uri}", failure)
            is EgressException -> EgressException(failure.isClientError, "Failed calling $uri", failure)
            else -> failure
        }
    }

    companion object {
        private const val OPPTJENINGSGRUNNLAG_PATH = "/popp/api/opptjeningsgrunnlag"
        private const val PING_PATH = "$OPPTJENINGSGRUNNLAG_PATH/ping"
        private val service = EgressService.PENSJONSOPPTJENING

        private fun emptyDto() = OpptjeningsgrunnlagResponseDto(OpptjeningsgrunnlagDto(emptyList()))
    }
}
