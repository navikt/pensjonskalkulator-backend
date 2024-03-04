package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.tech.security.egress.AuthType
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer.JwtBearerEgressTokenService
import org.springframework.stereotype.Component

@Component
class EgressAccessTokenFacade(
    private val clientCredentialsTokenService: ClientCredentialsEgressTokenService,
    private val jwtBearerTokenService: JwtBearerEgressTokenService
) {

    fun getAccessToken(authType: AuthType, audience: String): RawJwt =
        tokenGetter(authType).getEgressToken("", audience, "")

    private fun tokenGetter(authType: AuthType): EgressTokenGetter =
        when (authType) {
            AuthType.MACHINE_INSIDE_NAV -> clientCredentialsTokenService
            AuthType.MACHINE_OUTSIDE_NAV -> jwtBearerTokenService
            else -> unsupported(authType)
        }

    companion object {
        private fun <T> unsupported(authType: AuthType): T {
            throw IllegalArgumentException("Unsupported auth type: $authType")
        }
    }
}
