package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.onbehalfof

import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdUtil.getDefaultScope
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import org.springframework.stereotype.Service

@Service
class AzureAdOnBehalfOfEgressTokenService(
    val tokenGetter: AzureAdOnBehalfOfClient,
    private val navIdExtractor: SecurityContextNavIdExtractor
) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt {
        val scope = getDefaultScope(audience)

        val accessParameter = ingressToken?.let(TokenAccessParameter::onBehalfOf)
            ?: throw IllegalArgumentException("Missing ingressToken for OBO flow")

        val navIdent = navIdExtractor.id().ifEmpty { "unknown" }
        val tokenValue = tokenGetter.getTokenData(accessParameter, scope, navIdent).accessToken
        return RawJwt(tokenValue)
    }
}
