package no.nav.pensjon.kalkulator.tp.client.esb

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.GatewayUsage
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.UsernameTokenClient
import no.nav.pensjon.kalkulator.tp.client.esb.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tp.client.TjenestepensjonClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Component
class EsbTjenestepensjonClient(
    @Value("\${tp.url}") private val baseUrl: String,
    private val usernameTokenClient: UsernameTokenClient,
    @Qualifier("soap") private val webClient: WebClient,
    private val xmlMapper: XmlMapper
) : TjenestepensjonClient {
    private val log = KotlinLogging.logger {}

    override fun harTjenestepensjonsforhold(pid: Pid): Boolean {
        val responseXml = fetchTjenestepensjonsforholdXml(pid)

        return try {
            val dto = xmlMapper.readValue(responseXml, EnvelopeDto::class.java)
            isForholdDefined(dto)
        } catch (e: JsonProcessingException) {
            log.error(e) { "Failed to process XML: $responseXml" }
            false
        }
    }

    private fun fetchTjenestepensjonsforholdXml(pid: Pid): String {
        val uri = "$baseUrl$PATH"
        val body = soapEnvelope(pid.value, soapBody(pid))
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
        return """<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    ${soapHeader(userId)}
    $body
</soapenv:Envelope>"""
    }

    private fun soapHeader(userId: String): String {
        val callId = callId()

        return """<soapenv:Header>
        <callId xmlns="uri:no.nav.applikasjonsrammeverk">$callId</callId>
        <sc:StelvioContext xmlns="" xmlns:sc="http://www.nav.no/StelvioContextPropagation">
            <applicationId>$APPLICATION_USER_ID</applicationId>
            <correlationId>$callId</correlationId>
            <userId>$userId</userId>
        </sc:StelvioContext>
        ${usernameToken()}
    </soapenv:Header>"""
    }

    private fun usernameToken() = usernameTokenClient.fetchUsernameToken().token

    companion object {
        private const val PATH = "/nav-cons-pen-pselv-tjenestepensjonWeb/sca/PSELVTjenestepensjonWSEXP"

        // PP01 is the old service user ID for pensjon (replaced by srvpensjon/srvpselv in most apps)
        private const val APPLICATION_USER_ID = "PP01"

        private val service = EgressService.TJENESTEPENSJONSFORHOLD

        private fun callId() = UUID.randomUUID().toString()

        private fun setHeaders(headers: HttpHeaders) {
            if (service.gatewayUsage == GatewayUsage.INTERNAL) {
                headers.setBearerAuth(EgressAccess.token(service).value)
            }

            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun soapBody(pid: Pid): String {
            return """<soapenv:Body>
        ${specXml(pid)}
    </soapenv:Body>"""
        }

        private fun specXml(pid: Pid): String {
            return """<inf:finnTjenestepensjonForhold xmlns:inf="http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf">
        <finnTjenestepensjonForholdRequest>
          <hentSamhandlerInfo>true</hentSamhandlerInfo>
          <fnr>${pid.value}</fnr>
        </finnTjenestepensjonForholdRequest>
      </inf:finnTjenestepensjonForhold>"""
        }

        private fun isForholdDefined(dto: EnvelopeDto?) =
            dto?.body?.wrapper?.response?.forhold != null
    }
}
