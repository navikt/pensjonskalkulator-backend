package no.nav.pensjon.kalkulator.tech.security.ingress.ping

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Client for pinging ID-porten
 */
@Component
class IdPortenPingClient(
    @Value("\${idporten.ping.url}") private val uri: String,
    webClientBuilder: WebClient.Builder,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable {

    private val webClient = webClientBuilder.baseUrl(uri).build()

    override fun service() = service

    override fun ping(): PingResult {
        if (status == ServiceStatus.UP) {
            // Since this is a service outside Nav, we check status only once if UP
            return PingResult(service, ServiceStatus.UP, uri, "(cached status)")
        }

        return try {
            val responseBody = webClient
                .get()
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""

            PingResult(service, ServiceStatus.UP, uri, responseBody.substring(0, 150) + "...")
                .also { status = ServiceStatus.UP }
        } catch (e: WebClientRequestException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "forespørsel feilet")
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    companion object {
        private val service = EgressService.ID_PORTEN
        private var status = ServiceStatus.DOWN
    }
}
