package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.onbehalfof

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterBuilder
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentials
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient

@Component
@Qualifier("on-behalf-of")
class AzureAdOnBehalfOfClient(
    @Value("\${azure.openid-config.token-endpoint}") tokenEndpoint: String,
    webClientBuilder: WebClient.Builder,
    expirationChecker: ExpirationChecker,
    private val credentials: ClientCredentials,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : CacheAwareTokenClient(
    webClientBuilder.baseUrl(tokenEndpoint).build(),
    expirationChecker,
    retryAttempts
) {

    override fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter,
        audience: String
    ): MultiValueMap<String, String> =
        OAuth2ParameterBuilder()
            .tokenAccessParameter(accessParameter)
            .clientId(credentials.clientId)
            .clientSecret(credentials.clientSecret)
            .tokenAudience(audience)
            .buildOnBehalfOfTokenRequestMap()
}
