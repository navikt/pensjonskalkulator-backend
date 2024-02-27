package no.nav.pensjon.kalkulator.tech.security.egress.maskinporten.dev

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
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
 * Client for accessing the 'pensjonssimulator' service (see https://github.com/navikt/pensjonssimulator).
 * This is only used for testing in the development environment.
 */
@Component
class SimulatorDevClient(
    @Value("\${pensjonssimulering.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
) : ExternalServiceClient("0") {

    private val log = KotlinLogging.logger {}

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    override fun service() = service

    fun status(): String {
        val uri = "/v1/status"
        log.debug { "GET from URI: '$uri'" }

        return try {
            webClient
                .get()
                .uri(uri)
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    fun tidligstMuligUttak(): AlderV1 {
        val uri = "/v1/tidligst-mulig-uttak"
        log.debug { "POST to URI: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(BODY)
                .retrieve()
                .bodyToMono(AlderV1::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: AlderV1(0, 0)
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val BODY = """{
    "personId": "12906498357",
    "fodselsdato": "1964-10-12",
    "uttaksgrad": 100,
    "heltUttakFraOgMedDato": null,
    "rettTilAfpOffentligDato": null,
    "antallAarUtenlandsEtter16Aar": 0,
    "fremtidigInntektListe": [
        {
            "fraOgMedDato": "2030-01-01",
            "arligInntekt": 500000
        }
    ]
}"""

        private val service = EgressService.PENSJONSSIMULERING
    }
}

data class AlderV1(
    val aar: Int,
    val maaneder: Int
)
