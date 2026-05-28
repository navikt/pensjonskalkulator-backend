package no.nav.pensjon.kalkulator.opptjening.client.popp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.opptjening.Pensjonspoeng
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengRequestDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.map.PensjonspoengMapper
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
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
import reactor.core.publisher.Mono

@Component
class PoppPensjonspoengClient(
    @Value("\${popp.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), PensjonspoengClient, Pingable {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override fun service() = service

    override fun fetchPensjonspoeng(pid: Pid): List<Pensjonspoeng> {
        val url = "$baseUrl/$PATH"
        log.debug { "POST to URL: '$url'" }

        return try {
            val requestBody = PensjonspoengRequestDto(fnr = pid.value)

            webClient
                .post()
                .uri("/$PATH")
                .headers { setHeaders(it, pid) }
                .body(Mono.just(requestBody), PensjonspoengRequestDto::class.java)
                .retrieve()
                .bodyToMono(PensjonspoengResponseDto::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(PensjonspoengMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: emptyList()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val url = "$baseUrl/$PING_PATH"

        return try {
            val responseBody = webClient
                .get()
                .uri("/$PING_PATH")
                .headers(::setPingHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?: ""

            PingResult(service, ServiceStatus.UP, url, responseBody)
        } catch (e: WebClientRequestException) {
            PingResult(service, ServiceStatus.DOWN, url, e.message ?: "forespørsel feilet")
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, url, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[CustomHttpHeaders.PID] = pid.value
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val PATH = "popp/api/pensjonspoeng/hent"
        private const val PING_PATH = "popp/api/pensjonspoeng/ping"
        private val service = EgressService.PENSJONSOPPTJENING
    }
}
