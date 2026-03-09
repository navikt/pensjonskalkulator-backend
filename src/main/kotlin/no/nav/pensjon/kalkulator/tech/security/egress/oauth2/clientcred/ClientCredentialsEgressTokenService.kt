package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred

import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdUtil.getDefaultScope
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenDataGetter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ClientCredentialsEgressTokenService(
    @param:Qualifier("client-credentials") val tokenGetter: TokenDataGetter
) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String): RawJwt {
        val scope = getDefaultScope(audience)
        val accessParameter = TokenAccessParameter.clientCredentials(scope)
        val tokenValue = tokenGetter.getTokenData(accessParameter, scope, USER).accessToken
        return RawJwt(tokenValue)
    }

    companion object {
        private const val USER = "application"
    }
}
