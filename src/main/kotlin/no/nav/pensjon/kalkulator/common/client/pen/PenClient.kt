package no.nav.pensjon.kalkulator.common.client.pen

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.PingableServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

abstract class PenClient(
    baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    retryAttempts: String
) : PingableServiceClient(baseUrl, webClientBuilder, retryAttempts) {

    private val log = KotlinLogging.logger {}

    override fun pingPath(): String = PING_PATH

    override fun service(): EgressService = service

    protected fun <T> doGet(
        elementTypeRef: ParameterizedTypeReference<T>,
        path: String,
        pid: Pid
    ): T? {
        val uri = "/$BASE_PATH/$path"
        log.debug { "GET from URI: '$uri'" }

        return try {
            webClient
                .get()
                .uri(uri)
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(elementTypeRef)
                .retryWhen(retryBackoffSpec(uri))
                .block().also { countCalls(MetricResult.OK) }
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
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
        val uri = "/$BASE_PATH/$path"
        log.debug { "POST to URI: '$uri'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .body(Mono.just(requestBody), requestClass)
                .retrieve()
                .bodyToMono(responseClass)
                .retryWhen(retryBackoffSpec(uri))
                .block()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $service", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service()).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders, pid: Pid? = null) {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        pid?.let { headers[CustomHttpHeaders.PID] = it.value }
    }

    companion object {
        private const val BASE_PATH = "pen/springapi"
        private const val PING_PATH = "$BASE_PATH/ping"
        private val service = EgressService.PENSJONSFAGLIG_KJERNE
    }
}
