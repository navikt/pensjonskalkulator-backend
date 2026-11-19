package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.AuthType
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenTypeDecisionBasis
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer.JwtBearerEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.onbehalfof.OnBehalfOfEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.TokenExchangeService
import org.springframework.stereotype.Component

/**
 * Supports all types of access token for calls to other applications (egress calls).
 */
@Component
class EgressAccessTokenFacade(
    private val clientCredentialsTokenService: ClientCredentialsEgressTokenService,
    private val jwtBearerTokenService: JwtBearerEgressTokenService,
    private val tokenExchangeService: TokenExchangeService,
    private val onBehalfOfTokenService: OnBehalfOfEgressTokenService
) {
    fun getAccessToken(
        authType: AuthType,
        audience: String,
        tokenTypeDecisionBasis: EgressTokenTypeDecisionBasis
    ): RawJwt =
        tokenGetter(authType, tokenTypeDecisionBasis.representasjonTarget.rolle)
            .getEgressToken(tokenTypeDecisionBasis.ingressToken, audience)

    private fun tokenGetter(authType: AuthType, rolle: RepresentertRolle): EgressTokenGetter =
        when (authType) {
            AuthType.MACHINE_INSIDE_NAV -> clientCredentialsTokenService
            AuthType.MACHINE_OUTSIDE_NAV -> jwtBearerTokenService
            AuthType.NAV_ANSATT -> onBehalfOfTokenService
            AuthType.PERSON_SELF -> tokenExchangeService
            AuthType.PERSONAL -> tokenGetter(rolle, stoetterTokenX = true)
            AuthType.PERSONAL_NOT_TOKEN_X -> tokenGetter(rolle, stoetterTokenX = false)
            AuthType.MUTUAL_TLS -> NoTokenGetter()
            AuthType.NONE -> NoTokenGetter()
        }

    private fun tokenGetter(rolle: RepresentertRolle, stoetterTokenX: Boolean): EgressTokenGetter =
        when (rolle) {
            RepresentertRolle.UNDER_VEILEDNING -> onBehalfOfTokenService
            RepresentertRolle.FULLMAKT_GIVER -> clientCredentialsTokenService
            else -> if (stoetterTokenX) tokenExchangeService else clientCredentialsTokenService
        }
}