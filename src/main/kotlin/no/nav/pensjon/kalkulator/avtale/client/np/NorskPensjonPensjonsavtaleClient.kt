package no.nav.pensjon.kalkulator.avtale.client.np

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.Alder
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.map.PensjonsavtaleMapper.fromDto
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.GatewayUsage
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class NorskPensjonPensjonsavtaleClient(
    @Value("\${norsk-pensjon.url}") private val baseUrl: String,
    private val samlTokenClient: SamlTokenClient,
    @Qualifier("soap") private val webClient: WebClient
) : PensjonsavtaleClient {
    private val log = KotlinLogging.logger {}

    override fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtale {
        val responseXml = fetchAvtalerXml(spec)

        return try {
            val dto: EnvelopeDto = xmlMapper().readValue(responseXml, EnvelopeDto::class.java)
            fromDto(dto)
        } catch (e: JsonProcessingException) {
            log.error(e) { "Failed to process JSON" }
            emptyAvtale()
        }
    }

    private fun fetchAvtalerXml(spec: PensjonsavtaleSpec): String {
        val uri = "$baseUrl$PATH"
        val body = soapEnvelope(spec.pid.value, soapBody(spec))
        log.debug { "POST to URI: '$uri' with body '$body'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
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

    private fun soapEnvelope(userId: String, body: String): String {
        return """<?xml version="1.0" ?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    ${soapHeader(userId)}
    $body
</S:Envelope>"""
    }

    private fun soapHeader(userId: String): String {
        val callId = callId()

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

    companion object {
        private const val PATH = "/privat.pensjonsrettighetstjeneste/privatPensjonTjenesteV2_0"

        // PP01 is the old service user ID for pensjon (replaced by srvpensjon/srvpselv in most apps)
        const val APPLICATION_USER_ID = "PP01"

        private val service = EgressService.PENSJONSAVTALER
        private fun callId() = UUID.randomUUID().toString()

        private fun setHeaders(headers: HttpHeaders) {
            if (service.gatewayUsage == GatewayUsage.INTERNAL) {
                headers.setBearerAuth(EgressAccess.token(service).value)
            }

            headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun soapBody(spec: PensjonsavtaleSpec): String {
            return """<S:Body>
        ${xml(spec)}
    </S:Body>"""
        }

        private fun xml(spec: PensjonsavtaleSpec): String {
            return """<np:rettighetshaver xmlns:np="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <foedselsnummer xmlns="">14117940749</foedselsnummer>
            <aarligInntektFoerUttak xmlns="">${spec.aarligInntektFoerUttak}</aarligInntektFoerUttak>
            ${xml(spec.uttaksperiode)}
            <antallInntektsaarEtterUttak xmlns="">${spec.antallInntektsaarEtterUttak}</antallInntektsaarEtterUttak>
        </np:rettighetshaver>"""
        }

        private fun xml(spec: UttaksperiodeSpec): String {
            return """<uttaksperiode xmlns="">
                <startAlder>${spec.startAlder}</startAlder>
                <startMaaned>${spec.startMaaned}</startMaaned>
                <grad>${spec.grad}</grad>
                <aarligInntekt>${spec.aarligInntekt}</aarligInntekt>
            </uttaksperiode>"""
        }

        private fun xmlMapper(): XmlMapper {
            val mapper = XmlMapper()
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            return mapper
        }

        private fun emptyAvtale() = Pensjonsavtale("", "", 0, null, emptyUtbetalingsperiode())

        private fun emptyUtbetalingsperiode() = Utbetalingsperiode(Alder(0, 0), null, 0, 0)

        /* TODO error handling
            private fun getFaultMessage(envelopeXml: String): String {
                return try {
                    val envelope: EnvelopeDto = xmlMapper().readValue(envelopeXml, EnvelopeDto::class.java)
                    getFaultMessage(envelope, envelopeXml)
                } catch (e: JsonProcessingException) {
                    log.error("Failed to process fault message '{}'", envelopeXml, e)
                    envelopeXml
                }
            }

            private fun getFaultMessage(envelope: EnvelopeDto, defaultMessage: String): String {
                return if (isFaultless(envelope)) defaultMessage else toString(envelope.body.fault)
            }

            private fun isFaultless(envelope: EnvelopeDto?): Boolean {
                return envelope == null || envelope.body == null || envelope.body.fault == null
            }

            private fun toString(fault: FaultDto): String {
                return format(
                    "Code: %s | String: %s | Detail: { %s }",
                    fault.faultcode,
                    fault.faultstring,
                    toString(fault.detail)
                )
            }

            private fun toString(detail: DetailDto): String {
                val errorDetail: ErrorDetailDto =
                    if (detail.personNotFoundError == null) detail.samboerNotFoundError else detail.personNotFoundError
                return if (errorDetail == null) "" else toString(errorDetail)
            }

            private fun toString(errorDetail: ErrorDetailDto): String {
                return format(
                    "Message: %s | Source: %s | Type: %s | Root cause: %s | Timestamp: %s",
                    errorDetail.errorMessage,
                    errorDetail.errorSource,
                    errorDetail.errorType,
                    errorDetail.rootCause,
                    errorDetail.dateTimeStamp
                )
            }*/
    }
}
