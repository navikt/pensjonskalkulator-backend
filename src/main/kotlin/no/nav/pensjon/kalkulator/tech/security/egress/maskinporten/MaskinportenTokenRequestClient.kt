package no.nav.pensjon.kalkulator.tech.security.egress.maskinporten

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterBuilder
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClient2
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient

@Component
class MaskinportenTokenRequestClient(
    webClientBuilder: WebClient.Builder,
    @Value("\${maskinporten.token.endpoint}") tokenEndpoint: String,
    expirationChecker: ExpirationChecker,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : CacheAwareTokenClient2(
    webClientBuilder.baseUrl(tokenEndpoint).build(),
    tokenEndpoint,
    expirationChecker,
    retryAttempts
) {

    override fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter,
        audience: String
    ): MultiValueMap<String, String> =
        OAuth2ParameterBuilder()
            .tokenAccessParameter(accessParameter)
            .clientAssertionTokenRequestMap()
}
