package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred

import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdUtil.getDefaultScope
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import org.springframework.stereotype.Service

@Service
class ClientCredentialsEgressTokenService(val tokenGetter: ClientCredentialsTokenRequestClient) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt {
        val scope = getDefaultScope(audience)
        val accessParameter = TokenAccessParameter.clientCredentials(scope)
        val tokenValue = tokenGetter.getTokenData(accessParameter, scope, USER).accessToken
        return RawJwt(tokenValue)
    }

    companion object {
        private const val USER = "application"
    }
}
