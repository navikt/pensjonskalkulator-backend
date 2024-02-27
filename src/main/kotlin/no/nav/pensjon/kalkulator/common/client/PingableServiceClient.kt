package no.nav.pensjon.kalkulator.common.client

import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

abstract class PingableServiceClient(
    private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable {

    abstract fun pingPath(): String

    abstract fun setPingHeaders(headers: HttpHeaders)

    protected val webClient: WebClient = webClientBuilder.baseUrl(baseUrl).build()

    override fun ping(): PingResult {
        val url = "$baseUrl/${pingPath()}"

        return try {
            val responseBody = webClient
                .get()
                .uri("/${pingPath()}")
                .headers(::setPingHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?: ""

            PingResult(service(), ServiceStatus.UP, url, responseBody)
        } catch (e: EgressException) {
            // Happens if failing to obtain access token
            down(url, e)
        } catch (e: WebClientRequestException) {
            down(url, e)
        } catch (e: WebClientResponseException) {
            down(url, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun down(uri: String, e: Throwable) = down(uri, e.message ?: "Failed calling ${service()}")

    private fun down(uri: String, message: String) = PingResult(service(), ServiceStatus.DOWN, uri, message)
}
