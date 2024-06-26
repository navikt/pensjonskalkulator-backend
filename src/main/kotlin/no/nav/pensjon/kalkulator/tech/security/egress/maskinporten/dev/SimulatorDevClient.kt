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
    @Value("\${pensjonssimulator.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
) : ExternalServiceClient("0") {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override fun service() = service

    fun status(): String {
        val url = "$baseUrl/$STATUS_RESOURCE"
        log.debug { "GET from URL: '$url'" }

        return try {
            webClient
                .get()
                .uri("/$STATUS_RESOURCE")
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?: ""
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    fun tidligstMuligUttak(): TmuResult? {
        val url = "$baseUrl/$TIDLIGST_MULIG_UTTAK_RESOURCE"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$TIDLIGST_MULIG_UTTAK_RESOURCE")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(TMU_BODY)
                .retrieve()
                .bodyToMono(TmuResult::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    fun alderspensjon(): String? {
        val url = "$baseUrl/$SIMULER_ALDERSPENSJON_RESOURCE"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$SIMULER_ALDERSPENSJON_RESOURCE")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(ALDERSPENSJON_BODY)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    fun folketrygdbeholdning(): String? {
        val url = "$baseUrl/$SIMULER_FOLKETRYGDBEHOLDNING_RESOURCE"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$SIMULER_FOLKETRYGDBEHOLDNING_RESOURCE")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(FOLKETRYGDBEHOLDNING_BODY)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
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
        private const val STATUS_RESOURCE = "api/v1/status"
        private const val TIDLIGST_MULIG_UTTAK_RESOURCE = "api/v1/tidligst-mulig-uttak"
        private const val SIMULER_ALDERSPENSJON_RESOURCE = "api/v4/simuler-alderspensjon"
        private const val SIMULER_FOLKETRYGDBEHOLDNING_RESOURCE = "api/v1/simuler-folketrygdbeholdning"

        private const val TMU_BODY = """{
    "personId": "12906498357",
    "fodselsdato": "1964-10-12",
    "uttaksgrad": 100,
    "heltUttakFraOgMedDato": "2030-11-01",
    "rettTilAfpOffentligDato": null,
    "antallAarUtenlandsEtter16Aar": 0,
    "fremtidigInntektListe": [
        {
            "fraOgMedDato": "2030-01-01",
            "arligInntekt": 500000
        }
    ]
}"""

        private const val ALDERSPENSJON_BODY = """{
    "personId": "12906498357",
    "gradertUttak": {
        "fraOgMedDato": "2031-01-01",
        "uttaksgrad": 50
    },
    "heltUttakFraOgMedDato": "2034-10-12",
    "epsPensjon": false,
    "eps2G": false,
    "arIUtlandetEtter16": 0,
    "fremtidigInntektListe": [
        {
            "arligInntekt": 500000,
            "fraOgMedDato": "2030-01-01"
        }
    ],
    "rettTilAfpOffentligDato": "2032-01-01"
}"""

        private const val FOLKETRYGDBEHOLDNING_BODY = """{
    "personId": "12906498357",
    "uttaksdato": "1964-10-12",
    "arIUtlandetEtter16": 0,
    "epsPensjon": false,
    "eps2G": false,
    "fremtidigInntektListe": [
        {
            "fraOgMedDato": "2030-01-01",
            "arligInntekt": 500000
        }
    ]
}"""

        private val service = EgressService.PENSJONSSIMULATOR
    }
}


data class TmuResult(val tidligstMuligeUttakstidspunktListe: List<Tmu>, val feil: String?)
data class Tmu(val uttaksgrad: Int, val tidligstMuligeUttaksdato: String)
