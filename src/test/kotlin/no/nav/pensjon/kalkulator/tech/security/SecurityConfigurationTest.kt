package no.nav.pensjon.kalkulator.tech.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class SecurityConfigurationTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Test
    fun `when no 'fnr' header then isImpersonal returns false`() {
        `when`(request.getHeader("fnr")).thenReturn(null)
        assertFalse(SecurityConfiguration.isImpersonal(request))
    }

    @Test
    fun `when empty 'fnr' header then isImpersonal returns false`() {
        `when`(request.getHeader("fnr")).thenReturn("")
        assertFalse(SecurityConfiguration.isImpersonal(request))
    }

    @Test
    fun `when non-empty 'fnr' header then isImpersonal returns true`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        assertTrue(SecurityConfiguration.isImpersonal(request))
    }
}
