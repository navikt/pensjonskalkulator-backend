package no.nav.pensjon.kalkulator.avtale.client.np

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.map.PensjonsavtaleMapper.fromDto
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.GatewayUsage
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.nio.charset.StandardCharsets
import java.util.*

//@Component <--- Using v3 of client instead
class NorskPensjonPensjonsavtaleClient(
    @Value("\${norsk-pensjon.url}") private val baseUrl: String,
    private val samlTokenClient: SamlTokenClient,
    @Qualifier("soap") private val webClient: WebClient,
    private val xmlMapper: XmlMapper,
    private val callIdGenerator: CallIdGenerator
) : PensjonsavtaleClient {
    private val log = KotlinLogging.logger {}

    override fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        val responseXml = fetchAvtalerXml(spec)

        return try {
            val dto = xmlMapper.readValue(responseXml, EnvelopeDto::class.java)
            fromDto(dto)
        } catch (e: JsonProcessingException) {
            log.error(e) { "Failed to process JSON" }
            ingenAvtaler()
        }
    }

    private fun fetchAvtalerXml(spec: PensjonsavtaleSpec): String {
        val uri = "$baseUrl$PATH"
        val callId = callIdGenerator.newId()
        val body = soapEnvelope(spec.pid.value, soapBody(spec), callId)
        log.debug { "POST to URI: '$uri' with body '$body'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { setHeaders(it, callId) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to GET $uri: ${e.message}", e)
        }
    }

    private fun soapEnvelope(userId: String, body: String, callId: String): String {
        return """<?xml version="1.0" ?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    ${soapHeader(userId, callId)}
    $body
</S:Envelope>"""
    }

    private fun soapHeader(userId: String, callId: String): String {
        return """<SOAP-ENV:Header>
        <callId xmlns="uri:no.nav.applikasjonsrammeverk">$callId</callId>
        <sc:StelvioContext xmlns="" xmlns:sc="http://www.nav.no/StelvioContextPropagation">
            <applicationId>$APPLICATION_USER_ID</applicationId>
            <correlationId>$callId</correlationId>
            <userId>$userId</userId>
        </sc:StelvioContext>
        ${samlAssertion()}
    </SOAP-ENV:Header>"""
    }

    private fun samlAssertion() = String(Base64.getUrlDecoder().decode(samlTokenBytes()))

    private fun samlTokenBytes() = samlToken().toByteArray(StandardCharsets.UTF_8)

    private fun samlToken() = samlTokenClient.fetchSamlToken().access_token

    private fun setHeaders(headers: HttpHeaders, callId: String) {
        if (service.gatewayUsage == GatewayUsage.INTERNAL) {
            headers.setBearerAuth(EgressAccess.token(service).value)
        }

        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_XML_VALUE
        headers[CustomHttpHeaders.CALL_ID] = callId
    }

    companion object {
        private const val PATH = "/privat.pensjonsrettighetstjeneste/privatPensjonTjenesteV2_0"

        // PP01 is the old service user ID for pensjon (replaced by srvpensjon/srvpselv in most apps)
        private const val APPLICATION_USER_ID = "PP01"

        private val service = EgressService.PENSJONSAVTALER

        private fun soapBody(spec: PensjonsavtaleSpec): String {
            return """<S:Body>
        ${xml(spec)}
    </S:Body>"""
        }

        private fun xml(spec: PensjonsavtaleSpec): String {
            return """<np:rettighetshaver xmlns:np="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <foedselsnummer xmlns="">${spec.pid.value}</foedselsnummer>
            <aarligInntektFoerUttak xmlns="">${spec.aarligInntektFoerUttak}</aarligInntektFoerUttak>
            ${xml(spec.uttaksperioder)}
            <antallInntektsaarEtterUttak xmlns="">${spec.antallInntektsaarEtterUttak}</antallInntektsaarEtterUttak>
        </np:rettighetshaver>"""
        }

        private fun xml(specs: List<UttaksperiodeSpec>): String {
            return specs.joinToString("", transform = ::xml)
        }

        private fun xml(spec: UttaksperiodeSpec): String {
            return """<uttaksperiode xmlns="">
                <startAlder>${spec.start.aar}</startAlder>
                <startMaaned>${spec.start.maaned}</startMaaned>
                <grad>${spec.grad.prosentsats}</grad>
                <aarligInntekt>${spec.aarligInntekt}</aarligInntekt>
            </uttaksperiode>"""
        }

        private fun ingenAvtaler() = Pensjonsavtaler(emptyList(), emptyList())
    }
}
