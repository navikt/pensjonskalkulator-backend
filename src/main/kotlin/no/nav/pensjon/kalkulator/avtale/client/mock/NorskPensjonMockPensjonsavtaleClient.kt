package no.nav.pensjon.kalkulator.avtale.client.mock

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonResult
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonResultMapper
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonSpecMapper
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
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * Denne klienten skal hente mockede pensjonsavtaler i dev
 */
@Component
class NorskPensjonMockPensjonsavtaleClient(
    @param:Value($$"${norsk.pensjon.mock.url}") private val mockUrl: String,
    webClientBuilder: WebClient.Builder,
    val traceAid: TraceAid,
) {

    private val log = KotlinLogging.logger {}
    private val webClient = webClientBuilder.baseUrl(mockUrl).build()

    fun hent(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        val url = "$mockUrl/$PATH"
        log.info { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$PATH")
                .headers { setHeaders(it) }
                .bodyValue(NorskPensjonSpecMapper.toDto(spec))
                .retrieve()
                .bodyToMono<NorskPensjonResult>()
                .block()
                ?.let { NorskPensjonResultMapper.fromDto(it, spec.person.foedselsdato) }
                ?: Pensjonsavtaler(
                    avtaler = emptyList(),
                    utilgjengeligeSelskap = emptyList()
                )
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    private fun setHeaders(headers: HttpHeaders) {
        headers[HttpHeaders.ACCEPT] = MediaType.APPLICATION_JSON_VALUE
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object{
        const val PATH = "api/v3/pensjonsavtale/hent"
    }

}