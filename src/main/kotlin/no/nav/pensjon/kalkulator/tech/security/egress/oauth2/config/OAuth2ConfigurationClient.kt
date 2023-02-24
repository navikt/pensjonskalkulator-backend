package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config

import org.springframework.web.reactive.function.client.WebClient

open class OAuth2ConfigurationClient(private val webClient: WebClient, private val configUrl: String) : OAuth2ConfigurationGetter {

    private var cachedConfig: OAuth2ConfigurationDto? = null

    override fun getIssuer(): String {
        return getCachedConfig().getIssuer()!!
    }

    override fun getAuthorizationEndpoint(): String {
        return getCachedConfig().getAuthorizationEndpoint()!!
    }

    override fun getTokenEndpoint(): String {
        return getCachedConfig().getTokenEndpoint()!!
    }

    override fun getEndSessionEndpoint(): String {
        return getCachedConfig().getEndSessionEndpoint()!!
    }

    override fun getJsonWebKeySetUri(): String {
        return getCachedConfig().getJwksUri()!!
    }

    override fun refresh() {
        cachedConfig = null
    }

    private fun getFreshConfig(): OAuth2ConfigurationDto {
        return webClient
            .get()
            .uri(configUrl)
            .retrieve()
            .bodyToMono(OAuth2ConfigurationDto::class.java)
            .block()!!
    }

    private fun getCachedConfig(): OAuth2ConfigurationDto {
        return if (cachedConfig == null) getFreshConfig().also { cachedConfig = it } else cachedConfig!!
    }
}
