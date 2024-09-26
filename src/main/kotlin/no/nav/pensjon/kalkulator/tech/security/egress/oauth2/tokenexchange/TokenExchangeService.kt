package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange

import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdUtil.getDefaultScope
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.client.TokenExchangeClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheKey
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class TokenExchangeService(
    val client: TokenExchangeClient,
    private val pidGetter: PidGetter
) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt {
        val scope = getDefaultScope(audience)

        val accessParameter = ingressToken?.let(TokenAccessParameter::tokenExchange)
            ?: throw IllegalArgumentException("Missing ingressToken")

        val tokenValue = client.exchange(accessParameter, CacheKey(scope, pidGetter.pid())).accessToken
        return RawJwt(tokenValue)
    }
}
