package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterBuilder
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config.OAuth2ConfigurationGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient

@Component
class ClientCredentialsTokenRequestClient(
    webClient: WebClient,
    oauth2ConfigGetter: OAuth2ConfigurationGetter,
    expirationChecker: ExpirationChecker,
    private val credentials: ClientCredentials,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : CacheAwareTokenClient(
    webClient,
    oauth2ConfigGetter,
    expirationChecker,
    retryAttempts) {

    override fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter,
        audience: String
    ): MultiValueMap<String, String> =
        OAuth2ParameterBuilder()
            .tokenAccessParameter(accessParameter)
            .clientId(credentials.clientId)
            .clientSecret(credentials.clientSecret)
            .buildClientCredentialsTokenRequestMap()
}
