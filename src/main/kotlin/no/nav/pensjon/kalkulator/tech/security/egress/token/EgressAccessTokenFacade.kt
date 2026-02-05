package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.tech.security.egress.AuthType
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer.JwtBearerEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.onbehalfof.AzureAdOnBehalfOfEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.TokenExchangeService
import org.springframework.stereotype.Component

@Component
class EgressAccessTokenFacade(
    private val clientCredentialsTokenService: ClientCredentialsEgressTokenService,
    private val jwtBearerTokenService: JwtBearerEgressTokenService,
    private val tokenExchangeService: TokenExchangeService,
    private val azureAdOnBehalfOfTokenService: AzureAdOnBehalfOfEgressTokenService
) {
    fun getAccessToken(authType: AuthType, audience: String, ingressToken: String?): RawJwt =
        tokenGetter(authType).getEgressToken(ingressToken, audience, "")

    private fun tokenGetter(authType: AuthType): EgressTokenGetter =
        when (authType) {
            AuthType.MACHINE_INSIDE_NAV -> clientCredentialsTokenService
            AuthType.MACHINE_OUTSIDE_NAV -> jwtBearerTokenService
            AuthType.PERSON_SELF -> tokenExchangeService
            AuthType.PERSON_ON_BEHALF -> azureAdOnBehalfOfTokenService
            else -> unsupported(authType)
        }

    companion object {
        private fun <T> unsupported(authType: AuthType): T {
            throw IllegalArgumentException("Unsupported auth type: $authType")
        }
    }
}
