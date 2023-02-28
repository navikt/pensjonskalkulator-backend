package no.nav.pensjon.kalkulator.tech.security.egress

import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.function.Supplier

@ExtendWith(SpringExtension::class)
class EnrichedAuthenticationTest {

    private lateinit var enrichedAuthentication: EnrichedAuthentication

    @Mock
    private lateinit var initialAuth: Authentication

    @BeforeEach
    fun initialize() {
        val tokenSuppliersByService =
            EgressTokenSuppliersByService(mapOf(EgressService.PENSJON_REGLER to Supplier { RawJwt("token1") }))

        enrichedAuthentication = EnrichedAuthentication(initialAuth, tokenSuppliersByService)
    }

    @Test
    fun `getEgressAccessToken returns access token for given egress service`() {
        val token = enrichedAuthentication.getEgressAccessToken(EgressService.PENSJON_REGLER)
        assertEquals("token1", token.value)
    }

    @Test
    fun `verify that getters return values from wrapped class`() {
        `when`(initialAuth.name).thenReturn("name1")
        `when`(initialAuth.authorities).thenReturn(mutableListOf(GrantedAuthority { "authority1" }))
        `when`(initialAuth.credentials).thenReturn("credentials1")
        `when`(initialAuth.details).thenReturn("details1")
        `when`(initialAuth.principal).thenReturn("principal1")
        `when`(initialAuth.isAuthenticated).thenReturn(true)

        assertEquals("name1", enrichedAuthentication.name)
        assertEquals("authority1", enrichedAuthentication.authorities.first().authority)
        assertEquals("credentials1", enrichedAuthentication.credentials)
        assertEquals("details1", enrichedAuthentication.details)
        assertEquals("principal1", enrichedAuthentication.principal)
        assertTrue(enrichedAuthentication.isAuthenticated)
    }

    @Test
    fun `setAuthenticated sets the value in the wrapped class`() {
        enrichedAuthentication.isAuthenticated = false
        verify(initialAuth, times(1)).isAuthenticated = false
    }
}
