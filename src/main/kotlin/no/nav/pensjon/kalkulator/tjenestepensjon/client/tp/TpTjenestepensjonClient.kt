package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
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
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpTjenestepensjonDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map.TpTjenestepensjonMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.DefaultUriBuilderFactory
import java.time.LocalDate

/**
 * Client for accessing the 'tp' service (see https://github.com/navikt/tp)
 */
@Component
class TpTjenestepensjonClient(
    @Value("\${tjenestepensjon.url}") private val baseUrl: String,
    private val webClient: WebClient,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), TjenestepensjonClient, Pingable {

    override fun service() = service

    override fun harTjenestepensjonsforhold(pid: Pid, dato: LocalDate): Boolean {
        val uri = uri(dato)
        log.debug { "GET from URI: '$uri'" }

        return try {
            webClient
                .get()
                .uri(uri)
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(TpTjenestepensjonDto::class.java)
                .retryWhen(retryBackoffSpec(uri.toString()))
                .block()
                ?.let(TpTjenestepensjonMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: false
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val uri = "$baseUrl/$PING_PATH"

        return try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""

            PingResult(service, ServiceStatus.UP, uri, responseBody)
        } catch (e: WebClientRequestException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "Failed calling $uri")
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun uri(date: LocalDate) =
        DefaultUriBuilderFactory(baseUrl)
            .uriString("/$API_PATH/$API_RESOURCE")
            .queryParam("date", date.toString())
            .queryParam("ytelseType", TpYtelseType.ALDERSPENSJON.externalValue)
            .queryParam("ordningType", TpOrdningType.OFFENTLIG_TJENESTEPENSJONSORDNING.externalValue)
            .build()

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()

        // https://github.com/navikt/tp/blob/main/tp-api/src/main/kotlin/no/nav/samhandling/tp/provider/Headers.kt
        headers[CustomHttpHeaders.PID] = pid.value
        log.debug { "Calling TP for $pid" }
    }

    companion object {
        private const val API_PATH = "api/tjenestepensjon"
        private const val PING_PATH = "actuator/health/liveness"

        // https://github.com/navikt/tp/blob/main/tp-api/src/main/kotlin/no/nav/samhandling/tp/controller/TjenestepensjonController.kt
        private const val API_RESOURCE = "haveYtelse"

        private val service = EgressService.TJENESTEPENSJON
    }
}
