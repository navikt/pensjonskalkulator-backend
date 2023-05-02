package no.nav.pensjon.kalkulator.simulering.client.pen

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringRequestDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringResponseDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimulertAlderspensjonDto
import no.nav.pensjon.kalkulator.simulering.client.pen.map.SimuleringMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.SimuleringMapper.toDto
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.util.*

@Component
class PenSimuleringClient(
    @Value("\${pen.url}") private val baseUrl: String,
    private val webClient: WebClient
) : SimuleringClient, Pingable {
    private val log = KotlinLogging.logger {}

    override fun simulerAlderspensjon(spec: SimuleringSpec): Simuleringsresultat {
        val uri = "$baseUrl${SIMULERING_PATH}"
        log.debug { "POST to URI: '$uri'" }

        try {
            val response = webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .body(Mono.just(toDto(spec)), SimuleringRequestDto::class.java)
                .retrieve()
                .bodyToMono(SimuleringResponseDto::class.java)
                .block()
                ?: emptyDto()

            return SimuleringMapper.fromDto(response)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to GET $uri: ${e.message}", e)
        }
    }

    override fun ping(): PingResult {
        val uri = baseUrl + PING_PATH

        try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .headers { setPingHeaders(it) }
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

    companion object {
        private const val SIMULERING_PATH = "/pen/springapi/simulering/alderspensjon"
        private const val PING_PATH = "/api/ping"
        private val service = EgressService.SIMULERING

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun setPingHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun callId() = UUID.randomUUID().toString()

        private fun emptyDto() = SimuleringResponseDto(SimulertAlderspensjonDto(0, 0))
    }
}
