package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

open class OAuth2ConfigurationClient(
    private val uri: String,
    private val webClient: WebClient,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), OAuth2ConfigurationGetter, Pingable {

    private var cachedConfig: OAuth2ConfigurationDto? = null

    override fun service() = service

    override fun getIssuer(): String = cachedConfig().getIssuer()!!

    override fun getAuthorizationEndpoint(): String = cachedConfig().getAuthorizationEndpoint()!!

    override fun getTokenEndpoint(): String = cachedConfig().getTokenEndpoint()!!

    override fun getEndSessionEndpoint() = cachedConfig().getEndSessionEndpoint()!!

    override fun getJsonWebKeySetUri(): String = cachedConfig().getJwksUri()!!

    override fun refresh() {
        cachedConfig = null
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    override fun ping(): PingResult {
        if (status == ServiceStatus.UP) {
            // Since this is a service outside NAV, we check status only once if UP
            return PingResult(service, ServiceStatus.UP, uri, "(cached status)")
        }

        return try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""

            PingResult(service, ServiceStatus.UP, uri, responseBody.substring(0, 150) + "...")
                .also { status = ServiceStatus.UP }
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        }
    }

    private fun freshConfig(): OAuth2ConfigurationDto =
        webClient
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(OAuth2ConfigurationDto::class.java)
            .retryWhen(retryBackoffSpec(uri))
            .block()!!.also { countCalls(MetricResult.OK) }

    private fun cachedConfig(): OAuth2ConfigurationDto =
        if (cachedConfig == null)
            freshConfig().also { cachedConfig = it }
        else
            cachedConfig!!

    companion object {
        private val service = EgressService.MICROSOFT_ENTRA_ID
        private var status = ServiceStatus.DOWN
    }
}
