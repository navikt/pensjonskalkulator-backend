package no.nav.pensjon.kalkulator.regler

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.util.*

abstract class PensjonReglerClient(
    private val baseUrl: String,
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper,
    private val traceAid: TraceAid,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable {

    override fun service() = service

    fun <Req : Any, Res> doPost(
        path: String,
        requestBody: Req,
        requestClass: Class<Req>,
        responseClass: Class<Res>
    ): Res {
        val uri = "$baseUrl/$path"
        log.debug { "POST to URI: '$uri'" }

        try {
            val responseBody = webClient
                .post()
                .uri(uri)
                .headers { setHeaders(it) }
                .body(Mono.just(requestBody), requestClass)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""

            return objectMapper.readValue(responseBody, responseClass)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to do POST towards $baseUrl: ${e.message}", e)
        }
    }

    override fun ping(): PingResult {
        val uri = "$baseUrl/$PING_PATH"

        return try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .headers { setPingHeaders(it) }
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""

            return PingResult(service, ServiceStatus.UP, uri, responseBody)
        } catch (e: WebClientRequestException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "forespørsel feilet")
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val PING_PATH = "info"
        private val service = EgressService.PENSJON_REGLER
    }
}
