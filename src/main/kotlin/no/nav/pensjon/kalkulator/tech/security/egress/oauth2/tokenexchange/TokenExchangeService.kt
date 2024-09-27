package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange

import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdUtil.getDefaultScope
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.client.TokenExchangeClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheKey
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import org.springframework.stereotype.Service

@Service
class TokenExchangeService(
    val client: TokenExchangeClient,
    private val loggedInPidProvider: SecurityContextPidExtractor
) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt {
        val scope = getDefaultScope(audience)

        val accessParameter = ingressToken?.let(TokenAccessParameter::tokenExchange)
            ?: throw IllegalArgumentException("Missing ingressToken")

        val tokenValue = loggedInPidProvider.pid()?.let {
            client.exchange(accessParameter, CacheKey(scope, it)).accessToken
        } ?: throw IllegalStateException("Missing PID of logged in user")

        return RawJwt(tokenValue)
    }
}
