package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.onbehalfof

import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdUtil.getDefaultScope
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenDataGetter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class OnBehalfOfEgressTokenService(
    @param:Qualifier("on-behalf-of") val tokenGetter: TokenDataGetter,
    private val navIdGetter: SecurityContextNavIdExtractor
) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String): RawJwt {
        val scope = getDefaultScope(fullyQualifiedApplicationName = audience)

        val accessParameter = ingressToken?.let(TokenAccessParameter::jwtBearer)
            ?: throw IllegalArgumentException("Missing ingressToken for OBO flow")

        val navId = navIdGetter.id().ifEmpty { "unknown" }
        val tokenValue = tokenGetter.getTokenData(accessParameter, scope, user = navId).accessToken
        return RawJwt(tokenValue)
    }
}
