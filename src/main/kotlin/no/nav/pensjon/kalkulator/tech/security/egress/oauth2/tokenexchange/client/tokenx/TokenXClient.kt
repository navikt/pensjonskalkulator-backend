package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.client.tokenx

import com.nimbusds.jose.jwk.RSAKey
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterBuilder
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.TokenExchangeCredentials
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.client.TokenExchangeClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheKey
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient

@Component
@Qualifier("token-x")
class TokenXClient(
    @Value("\${token.x.token_endpoint}") private val tokenEndpoint: String,
    @Value("\${pkb.representasjon.token.x.audience}") private val tokenAudience: String,
    webClientBuilder: WebClient.Builder,
    expirationChecker: ExpirationChecker,
    private val credentials: TokenExchangeCredentials,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : CacheAwareTokenClient(
    webClientBuilder.baseUrl(tokenEndpoint).build(),
    expirationChecker,
    retryAttempts
), TokenExchangeClient {

    override fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter,
        audience: String // to be used when more than one TokenX audience used
    ): MultiValueMap<String, String> =
        OAuth2ParameterBuilder()
            .tokenAccessParameter(accessParameter)
            .clientId(credentials.clientId)
            .tokenAudience(tokenAudience)
            .tokenRequestAudience(tokenEndpoint)
            .jwk(RSAKey.parse(credentials.jwk))
            .tokenExchangeRequestMap()

    override fun exchange(accessParameter: TokenAccessParameter, cacheKey: CacheKey) =
        getTokenData(accessParameter, scope = cacheKey.scope, user = cacheKey.pid.value)
}
