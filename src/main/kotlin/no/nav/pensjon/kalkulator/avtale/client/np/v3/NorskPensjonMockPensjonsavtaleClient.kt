package no.nav.pensjon.kalkulator.avtale.client.np.v3

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.NorskPensjonPensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
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
 * Denne klienten skal hente mockede pensjonsavtaler i dev
 */
@Component("norskPensjonMock")
class NorskPensjonMockPensjonsavtaleClient(
    @Value("\${norsk.pensjon.mock.url}") mockUrl: String,
    tokenGetter: SamlTokenService,
    webClientBuilder: WebClient.Builder,
    val traceAid: TraceAid,
    xmlMapper: XmlMapper,
    @Value("\${web-client.retry-attempts}") retryAttempts: String,
) : NorskPensjonPensjonsavtaleClient(
    baseUrl = mockUrl,
    tokenGetter = tokenGetter,
    webClientBuilder = webClientBuilder,
    xmlMapper = xmlMapper,
    traceAid = traceAid,
    retryAttempts = retryAttempts
) {

    override fun fetchAvtalerXml(spec: NorskPensjonPensjonsavtaleSpecDto): String {
        val uri = "/api/v1/pensjonsavtale"
        val callId = traceAid.callId()
        val body = soapEnvelope(soapBody(spec, callId))

        try {
            return webClient
                .post()
                .uri(uri)
                .headers {
                    it[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_XML_VALUE
                    it[CustomHttpHeaders.CALL_ID] = callId
                }
                .bodyValue(body)
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

    override fun soapHeader() =
        """<S:Header>
    </S:Header>"""

}