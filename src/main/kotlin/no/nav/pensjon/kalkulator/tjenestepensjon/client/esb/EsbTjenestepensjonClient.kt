package no.nav.pensjon.kalkulator.tjenestepensjon.client.esb

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.GatewayUsage
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.UsernameTokenClient
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import no.nav.pensjon.kalkulator.tjenestepensjon.client.esb.dto.EnvelopeDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration
import java.time.LocalDate
import java.util.*

/**
 * Client for accessing the 'tp' service via ESB (Enterprise Service Bus, tjenestebuss), using SOAP
 * See https://github.com/navikt/tp
 */
//@Component
class EsbTjenestepensjonClient(
    @Value("\${tp.url}") private val baseUrl: String,
    private val usernameTokenClient: UsernameTokenClient,
    @Qualifier("soap") private val webClient: WebClient,
    private val xmlMapper: XmlMapper,
    private val callIdGenerator: CallIdGenerator,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
    ) : TjenestepensjonClient {
    private val log = KotlinLogging.logger {}

    override fun harTjenestepensjonsforhold(pid: Pid, dato: LocalDate): Boolean {
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
        val callId = callIdGenerator.newId()
        val body = soapEnvelope(pid.value, soapBody(pid), callId)
        log.debug { "POST to URI: '$uri' with body '$body'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { setHeaders(it, callId) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(
                    Retry.backoff(retryAttempts.toLong(), Duration.ofSeconds(1))
                        .filter { it is EgressException && !it.isClientError }
                        .onRetryExhaustedThrow { backoff, signal -> handleFailure(backoff, signal) })
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    private fun setHeaders(headers: HttpHeaders, callId: String) {
        if (service.gatewayUsage == GatewayUsage.INTERNAL) {
            headers.setBearerAuth(EgressAccess.token(service).value)
        }

        headers[CustomHttpHeaders.CALL_ID] = callId
    }

    private fun soapEnvelope(userId: String, body: String, callId: String): String {
        return """<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    ${soapHeader(userId, callId)}
    $body
</soapenv:Envelope>"""
    }

    private fun soapHeader(userId: String, callId: String): String {

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

    private fun handleFailure(backoff: RetryBackoffSpec, retrySignal: Retry.RetrySignal): Throwable {
        log.info { "Retried calling $baseUrl$PATH ${backoff.maxAttempts} times" }

        return when (val failure = retrySignal.failure()) {
            is WebClientRequestException -> EgressException(true, "Failed calling ${failure.uri}", failure)
            is EgressException -> EgressException(failure.isClientError, "Failed calling $baseUrl$PATH", failure)
            else -> failure
        }
    }

    companion object {
        private const val PATH = "/nav-cons-pen-pselv-tjenestepensjonWeb/sca/PSELVTjenestepensjonWSEXP"

        // PP01 is the old service user ID for pensjon (replaced by srvpensjon/srvpselv in most apps)
        private const val APPLICATION_USER_ID = "PP01"

        private val service = EgressService.TJENESTEPENSJONSFORHOLD

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
