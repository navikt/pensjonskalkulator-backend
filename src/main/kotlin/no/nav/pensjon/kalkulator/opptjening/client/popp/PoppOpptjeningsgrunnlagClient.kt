package no.nav.pensjon.kalkulator.opptjening.client.popp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.map.OpptjeningsgrunnlagMapper
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Client for accessing the 'popp' service (see https://github.com/navikt/popp)
 */
@Component
class PoppOpptjeningsgrunnlagClient(
    @Value("\${popp.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), OpptjeningsgrunnlagClient, Pingable {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override fun service() = service

    override fun fetchOpptjeningsgrunnlag(pid: Pid): Opptjeningsgrunnlag {
        val url = "$baseUrl/$OPPTJENINGSGRUNNLAG_PATH"
        log.debug { "GET from URL: '$url'" }

        return try {
            webClient
                .get()
                .uri("/$OPPTJENINGSGRUNNLAG_PATH")
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(OpptjeningsgrunnlagResponseDto::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(OpptjeningsgrunnlagMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: Opptjeningsgrunnlag(emptyList())
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val url = "$baseUrl/$PING_PATH"

        return try {
            val responseBody = webClient
                .get()
                .uri("/$PING_PATH")
                .headers(::setPingHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?: ""

            PingResult(service, ServiceStatus.UP, url, responseBody)
        } catch (e: WebClientRequestException) {
            PingResult(service, ServiceStatus.DOWN, url, e.message ?: "forespørsel feilet")
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, url, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[CustomHttpHeaders.PID] = pid.value
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val OPPTJENINGSGRUNNLAG_PATH = "popp/api/opptjeningsgrunnlag"
        private const val PING_PATH = "$OPPTJENINGSGRUNNLAG_PATH/ping"
        private val service = EgressService.PENSJONSOPPTJENING
    }
}
