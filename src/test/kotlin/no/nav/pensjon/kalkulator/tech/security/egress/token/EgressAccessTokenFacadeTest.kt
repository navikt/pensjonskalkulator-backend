package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.tech.security.egress.AuthType
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer.JwtBearerEgressTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange.TokenExchangeService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class EgressAccessTokenFacadeTest {

    private lateinit var facade: EgressAccessTokenFacade

    @Mock
    private lateinit var clientCredentialsTokenService: ClientCredentialsEgressTokenService

    @Mock
    private lateinit var jwtBearerTokenService: JwtBearerEgressTokenService

    @Mock
    private lateinit var tokenExchangeService: TokenExchangeService

    @BeforeEach
    fun initialize() {
        facade = EgressAccessTokenFacade(
            clientCredentialsTokenService,
            jwtBearerTokenService,
            tokenExchangeService
        )
    }

    @Test
    fun `when user type is application then getAccessToken returns access token for application`() {
        `when`(
            clientCredentialsTokenService.getEgressToken(
                ingressToken = null,
                audience = AUDIENCE,
                user = ""
            )
        ).thenReturn(RawJwt(EGRESS_TOKEN))

        val token: RawJwt = facade.getAccessToken(
            authType = AuthType.MACHINE_INSIDE_NAV,
            audience = AUDIENCE,
            ingressToken = null
        )

        assertEquals(EGRESS_TOKEN, token.value)
    }

    companion object {
        private const val AUDIENCE = "audience1"
        private const val EGRESS_TOKEN = "token1"
    }
}
