package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Only used for self-test (testing the connection to the authorization provider).
 */
open class OAuth2ConfigurationClient(
    private val uri: String,
    webClientBuilder: WebClient.Builder,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable {

    private val webClient = webClientBuilder.baseUrl(uri).build()

    override fun service() = service

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

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

    companion object {
        private val service = EgressService.MICROSOFT_ENTRA_ID
        private var status = ServiceStatus.DOWN
    }
}
