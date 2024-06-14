package no.nav.pensjon.kalkulator.tech.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class SecurityConfigurationTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Test
    fun `when neither 'pid' attribute nor 'fnr' header then hasPid returns false`() {
        `when`(request.getAttribute("pid")).thenReturn(null)
        `when`(request.getHeader("fnr")).thenReturn(null)
        assertFalse(SecurityConfiguration.hasPid(request))
    }

    @Test
    fun `when empty 'pid' attribute and empty 'fnr' header then hasPid returns false`() {
        `when`(request.getAttribute("pid")).thenReturn("")
        `when`(request.getHeader("fnr")).thenReturn("")
        assertFalse(SecurityConfiguration.hasPid(request))
    }

    @Test
    fun `when empty 'pid' attribute and no 'fnr' header then hasPid returns false`() {
        `when`(request.getAttribute("pid")).thenReturn("")
        `when`(request.getHeader("fnr")).thenReturn(null)
        assertFalse(SecurityConfiguration.hasPid(request))
    }

    @Test
    fun `when valid 'pid' attribute then hasPid returns true`() {
        `when`(request.getAttribute("pid")).thenReturn("12906498357")
        `when`(request.getHeader("fnr")).thenReturn(null)
        assertTrue(SecurityConfiguration.hasPid(request))
    }

    @Test
    fun `when valid 'fnr' header then hasPid returns true`() {
        `when`(request.getAttribute("pid")).thenReturn("")
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        assertTrue(SecurityConfiguration.hasPid(request))
    }

    @Test
    fun `when different 'pid' attribute and 'fnr' header then hasPid throws exception`() {
        `when`(request.getAttribute("pid")).thenReturn("04925398980")
        `when`(request.getHeader("fnr")).thenReturn("12906498357")
        val exception = assertThrows<RuntimeException> { SecurityConfiguration.hasPid(request) }
        assertEquals("Ambiguous PID values", exception.message)
    }
}
