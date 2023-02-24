package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.tech.security.egress.UserType
import org.springframework.stereotype.Component

@Component
class EgressAccessTokenFacade(private val clientCredentialsEgressTokenGetter: EgressTokenGetter) {

    fun getAccessToken(userType: UserType, audience: String): RawJwt {
        return tokenGetter(userType).getEgressToken("", audience, "")
    }

    private fun tokenGetter(userType: UserType): EgressTokenGetter {
        return when (userType) {
            UserType.APPLICATION -> clientCredentialsEgressTokenGetter
            else -> unsupported(userType)
        }
    }

    companion object {
        private fun <T> unsupported(userType: UserType): T {
            throw IllegalArgumentException("Unsupported user type: $userType")
        }
    }
}
