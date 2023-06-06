package no.nav.pensjon.kalkulator.pen

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.util.*

abstract class PenClient(
    protected val baseUrl: String,
    private val webClient: WebClient,
) : Pingable {
    private val log = KotlinLogging.logger {}

    protected fun <Response> doGet(
        path: String,
        responseClass: Class<Response>
    ): Response? {
        val uri = baseUrl + path
        log.debug { "GET from URI: '$uri'" }

        try {
            return webClient
                .get()
                .uri(uri)
                .headers { setHeaders(it) }
                .retrieve()
                .bodyToMono(responseClass)
                .block()
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to do GET towards $baseUrl: ${e.message}", e)
        }
    }

    protected fun <Request : Any, Response> doPost(
        path: String,
        requestBody: Request,
        requestClass: Class<Request>,
        responseClass: Class<Response>
    ): Response? {
        val uri = baseUrl + path
        log.debug { "POST to URI: '$uri'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { setHeaders(it) }
                .body(Mono.just(requestBody), requestClass)
                .retrieve()
                .bodyToMono(responseClass)
                .block()
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to do POST towards $baseUrl: ${e.message}", e)
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
        const val BASE_PATH = "/pen/springapi"
        private const val PING_PATH = "/api/ping"
        private val service = EgressService.SIMULERING

        private fun callId() = UUID.randomUUID().toString()

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun setPingHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }
    }
}
