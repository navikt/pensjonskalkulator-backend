package no.nav.pensjon.kalkulator.tech.security.egress

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class SecurityContextEnricherTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var securityContextPidExtractor: SecurityContextPidExtractor

    @Test
    fun `enrichAuthentication tolerates null authentication`() {
        SecurityContextHolder.setContext(SecurityContextImpl(null))
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor)

        assertDoesNotThrow { enricher.enrichAuthentication(request) }
    }

    @Test
    fun `if PID in request header then enrichAuthentication does not get PID from security context`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor)

        enricher.enrichAuthentication(request)

        verify(securityContextPidExtractor, never()).pid()
    }

    @Test
    fun `if no PID in request header then enrichAuthentication gets PID from security context`() {
        `when`(request.getHeader("fnr")).thenReturn(null)
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor)

        enricher.enrichAuthentication(request)

        verify(securityContextPidExtractor, times(1)).pid()
    }

    private companion object {
        private fun tokenSuppliers() = EgressTokenSuppliersByService(emptyMap())
    }
}
