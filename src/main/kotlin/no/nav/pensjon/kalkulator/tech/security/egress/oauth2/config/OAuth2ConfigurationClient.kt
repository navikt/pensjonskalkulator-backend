package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.reactive.function.client.WebClient

open class OAuth2ConfigurationClient(
    private val uri: String,
    private val webClient: WebClient,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), OAuth2ConfigurationGetter {

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

    private fun freshConfig(): OAuth2ConfigurationDto =
        webClient
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(OAuth2ConfigurationDto::class.java)
            .retryWhen(retryBackoffSpec(uri))
            .block()!!

    private fun cachedConfig(): OAuth2ConfigurationDto =
        if (cachedConfig == null)
            freshConfig().also { cachedConfig = it }
        else
            cachedConfig!!

    companion object {
        private val service = EgressService.OAUTH2_CONFIGURATION
    }
}
