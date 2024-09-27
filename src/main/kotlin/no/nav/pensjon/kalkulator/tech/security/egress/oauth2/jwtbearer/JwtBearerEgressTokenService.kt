package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer

import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressTokenGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class JwtBearerEgressTokenService(
    @Qualifier("jwt-bearer") val tokenGetter: CacheAwareTokenClient,
    val assertionCreator: JwtBearerAssertionCreator
) : EgressTokenGetter {

    override fun getEgressToken(ingressToken: String?, audience: String, user: String): RawJwt {
        // audience = scope, e.g. "nav:pensjonssimulator:simulering"
        val accessParameter = TokenAccessParameter.jwtBearer(assertionCreator.assertion(audience))

        val tokenValue = tokenGetter
            .getTokenData(accessParameter = accessParameter, scope = "n/a", user = "n/a")
            .accessToken

        return RawJwt(tokenValue)
    }
}
