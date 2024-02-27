package no.nav.pensjon.kalkulator.common.client.fssgw

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Client for pinging the FSS gateway (see github.com/navikt/pensjon-selvbetjening-fss-gateway)
 * FSS = Fagsystemsonen
 */
@Component
class FssGatewayPingClient(
    @Value("\${proxy.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    override fun service() = service

    override fun ping(): PingResult {
        val url = "$baseUrl/$PATH"

        return try {
            val responseBody = webClient
                .get()
                .uri("/$PATH")
                .headers(::setHeaders)
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

    private fun setHeaders(headers: HttpHeaders) {
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    companion object {
        private const val PATH = "internal/ping"
        private val service = EgressService.FSS_GATEWAY
    }
}
