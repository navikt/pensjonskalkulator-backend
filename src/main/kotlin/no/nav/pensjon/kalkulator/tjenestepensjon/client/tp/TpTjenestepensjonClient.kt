package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.HarTjenestepensjonDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.net.URI
import java.time.Duration
import java.time.LocalDate

/**
 * Client for accessing the 'tp' service (see https://github.com/navikt/tp)
 */
@Component
class TpTjenestepensjonClient(
    @Value("\${tp.url}") private val baseUrl: String,
    private val webClient: WebClient,
    private val callIdGenerator: CallIdGenerator,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : TjenestepensjonClient {
    private val log = KotlinLogging.logger {}

    override fun harTjenestepensjonsforhold(pid: Pid, dato: LocalDate): Boolean {
        val uri = uri(dato)
        log.debug { "GET from URI: '$uri'" }

        try {
            val status = webClient
                .get()
                .uri(uri)
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(HarTjenestepensjonDto::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: emptyDto()

            return status.value
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    private fun uri(date: LocalDate) =
        DefaultUriBuilderFactory(baseUrl)
            .uriString("$API_PATH/$API_RESOURCE")
            .queryParam("date", date.toString())
            .queryParam("ytelseType", TpYtelseType.ALDERSPENSJON.externalValue)
            .queryParam("ordningType", TpOrdningType.OFFENTLIG_TJENESTEPENSJONSORDNING.externalValue)
            .build()

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = callIdGenerator.newId()

        // https://github.com/navikt/tp/blob/main/tp-api/src/main/kotlin/no/nav/samhandling/tp/provider/Headers.kt
        headers[CustomHttpHeaders.PID] = pid.value
    }

    private fun retryBackoffSpec(uri: URI): RetryBackoffSpec =
        Retry.backoff(retryAttempts.toLong(), Duration.ofSeconds(1))
            .filter { it is EgressException && !it.isClientError }
            .onRetryExhaustedThrow { backoff, signal -> handleFailure(backoff, signal, uri.toString()) }

    private fun handleFailure(backoff: RetryBackoffSpec, retrySignal: Retry.RetrySignal, uri: String): Throwable {
        log.info { "Retried calling $uri ${backoff.maxAttempts} times" }

        return when (val failure = retrySignal.failure()) {
            is WebClientRequestException -> EgressException(true, "Failed calling ${failure.uri}", failure)
            is EgressException -> EgressException(failure.isClientError, "Failed calling $uri", failure)
            else -> failure
        }
    }

    companion object {
        private const val API_PATH = "/api/tjenestepensjon"

        // https://github.com/navikt/tp/blob/main/tp-api/src/main/kotlin/no/nav/samhandling/tp/controller/TjenestepensjonController.kt
        private const val API_RESOURCE = "haveYtelse"

        private val service = EgressService.TJENESTEPENSJONSFORHOLD

        private fun emptyDto() = HarTjenestepensjonDto(false)
    }
}
