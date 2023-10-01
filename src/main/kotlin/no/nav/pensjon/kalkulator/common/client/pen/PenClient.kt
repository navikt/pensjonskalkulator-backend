package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

abstract class PenClient(
    private val baseUrl: String,
    private val webClient: WebClient,
    private val traceAid: TraceAid,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable {
    override fun service() = service

    protected fun <T> doGet(
        elementTypeRef: ParameterizedTypeReference<T>,
        path: String,
        pid: Pid
    ): T? {
        val uri = "$baseUrl/$BASE_PATH/$path"
        log.debug { "GET from URI: '$uri'" }

        return try {
            webClient
                .get()
                .uri(uri)
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(elementTypeRef)
                .retryWhen(retryBackoffSpec(uri))
                .block()
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    protected fun <Request : Any, Response> doPost(
        path: String,
        requestBody: Request,
        requestClass: Class<Request>,
        responseClass: Class<Response>
    ): Response? {
        val uri = "$baseUrl/$BASE_PATH/$path"
        log.debug { "POST to URI: '$uri'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { setHeaders(it) }
                .body(Mono.just(requestBody), requestClass)
                .retrieve()
                .bodyToMono(responseClass)
                .retryWhen(retryBackoffSpec(uri))
                .block()
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val uri = "$baseUrl/$PING_PATH"

        try {
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
        } catch (e: WebClientResponseException) {
            return PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        } catch (e: RuntimeException) {
            return PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "Ping failed")
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders, pid: Pid? = null) {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        pid?.let { headers[CustomHttpHeaders.PID] = it.value }
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val BASE_PATH = "pen/springapi"
        private const val PING_PATH = "api/ping"
        private val service = EgressService.PENSJONSFAGLIG_KJERNE
    }
}
