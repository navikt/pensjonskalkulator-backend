package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.tech.security.egress.UserType
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
    private lateinit var accessTokenGetterForClientCredentials: EgressTokenGetter

    @BeforeEach
    fun initialize() {
        facade = EgressAccessTokenFacade(accessTokenGetterForClientCredentials)
    }

    @Test
    fun `when user type is application then getAccessToken returns access token for application`() {
        `when`(accessTokenGetterForClientCredentials.getEgressToken("", AUDIENCE, "")).thenReturn(RawJwt(EGRESS_TOKEN))
        val token: RawJwt = facade.getAccessToken(UserType.APPLICATION, AUDIENCE)
        assertEquals(EGRESS_TOKEN, token.value)
    }

    companion object {
        private const val AUDIENCE = "audience1"
        private const val EGRESS_TOKEN = "token1"
    }
}
