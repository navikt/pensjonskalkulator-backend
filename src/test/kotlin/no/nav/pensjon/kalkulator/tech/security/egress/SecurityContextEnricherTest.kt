package no.nav.pensjon.kalkulator.tech.security.egress

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.crypto.PidEncryptionService
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class SecurityContextEnricherTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var securityContextPidExtractor: SecurityContextPidExtractor

    @Mock
    private lateinit var pidEncryptionService: PidEncryptionService

    @Mock
    private lateinit var authentication: Authentication

    @Test
    fun `enrichAuthentication tolerates null authentication`() {
        SecurityContextHolder.setContext(SecurityContextImpl(null))
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor, pidEncryptionService)

        assertDoesNotThrow { enricher.enrichAuthentication(request) }
    }

    @Test
    fun `if PID in request header then enrichAuthentication does not get PID from security context`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor, pidEncryptionService)

        enricher.enrichAuthentication(request)

        verify(securityContextPidExtractor, never()).pid()
    }

    @Test
    fun `if no PID in request header then enrichAuthentication gets PID from security context`() {
        `when`(request.getHeader("fnr")).thenReturn(null)
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor, pidEncryptionService)

        enricher.enrichAuthentication(request)

        verify(securityContextPidExtractor, times(1)).pid()
    }

    @Test
    fun `enrichAuthentication decrypts encrypted PID`() {
        SecurityContextHolder.setContext(SecurityContextImpl(authentication))
        `when`(request.getHeader("fnr")).thenReturn("encrypted.string.containing.dot")
        `when`(pidEncryptionService.decrypt("encrypted.string.containing.dot")).thenReturn("12906498357")
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor, pidEncryptionService)

        enricher.enrichAuthentication(request)

        assertEquals("12906498357", SecurityContextHolder.getContext().authentication?.enriched()?.pid?.value)
    }

    @Test
    fun `enrichAuthentication uses plaintext PID if not encrypted`() {
        SecurityContextHolder.setContext(SecurityContextImpl(authentication))
        `when`(request.getHeader("fnr")).thenReturn("12906498357")
        val enricher = SecurityContextEnricher(tokenSuppliers(), securityContextPidExtractor, pidEncryptionService)

        enricher.enrichAuthentication(request)

        assertEquals("12906498357", SecurityContextHolder.getContext().authentication?.enriched()?.pid?.value)
    }

    private companion object {
        private fun tokenSuppliers() = EgressTokenSuppliersByService(emptyMap())
    }
}
