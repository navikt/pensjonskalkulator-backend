package no.nav.pensjon.kalkulator.tech.security.egress

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.crypto.PidEncryptionService
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonService
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var securityContextPidExtractor: SecurityContextPidExtractor

    @Mock
    private lateinit var pidDecrypter: PidEncryptionService

    @Mock
    private lateinit var representasjonService: RepresentasjonService

    @Mock
    private lateinit var authentication: Authentication

    @Test
    fun `enrichAuthentication tolerates null authentication`() {
        SecurityContextHolder.setContext(SecurityContextImpl(null))
        assertDoesNotThrow { securityContextEnricher().enrichAuthentication(request, response) }
    }

    @Test
    fun `if PID in request header then enrichAuthentication does not get PID from security context`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        securityContextEnricher().enrichAuthentication(request, response)
        verify(securityContextPidExtractor, never()).pid()
    }

    @Test
    fun `if no PID in request header then enrichAuthentication gets PID from security context`() {
        SecurityContextHolder.setContext(SecurityContextImpl(authentication))
        `when`(request.getHeader("fnr")).thenReturn(null)

        securityContextEnricher().enrichAuthentication(request, response)

        verify(securityContextPidExtractor, times(1)).pid()
    }

    @Test
    fun `enrichAuthentication decrypts encrypted PID`() {
        SecurityContextHolder.setContext(SecurityContextImpl(authentication))
        `when`(request.getHeader("fnr")).thenReturn("encrypted.string.containing.dot")
        `when`(pidDecrypter.decrypt("encrypted.string.containing.dot")).thenReturn("12906498357")

        securityContextEnricher().enrichAuthentication(request, response)

        assertEquals("12906498357", securityContextTargetPid()?.value)
    }

    @Test
    fun `enrichAuthentication uses plaintext PID if not encrypted`() {
        SecurityContextHolder.setContext(SecurityContextImpl(authentication))
        `when`(request.getHeader("fnr")).thenReturn("12906498357")

        securityContextEnricher().enrichAuthentication(request, response)

        assertEquals("12906498357", securityContextTargetPid()?.value)
    }

    @Test
    fun `enrichAuthentication sets target PID from OBO cookie if valid representasjon`() {
        SecurityContextHolder.setContext(SecurityContextImpl(authentication))
        `when`(request.cookies).thenReturn(listOf(Cookie("nav-obo", "12906498357")).toTypedArray())
        `when`(representasjonService.hasValidRepresentasjonsforhold(Pid("12906498357"))).thenReturn(
            Representasjon(isValid = true, fullmaktGiverNavn = "F. Giver")
        )

        securityContextEnricher().enrichAuthentication(request, response)

        assertEquals("12906498357", securityContextTargetPid()?.value)
    }

    @Test
    fun `enrichAuthentication throws AccessDeniedException if invalid representasjon`() {
        SecurityContextHolder.setContext(SecurityContextImpl(authentication))
        `when`(request.cookies).thenReturn(listOf(Cookie("nav-obo", "12906498357")).toTypedArray())
        `when`(representasjonService.hasValidRepresentasjonsforhold(Pid("12906498357"))).thenReturn(
            Representasjon(isValid = false, fullmaktGiverNavn = "")
        )

        val exception = assertThrows<org.springframework.security.access.AccessDeniedException> {
            securityContextEnricher().enrichAuthentication(request, response)
        }

        assertEquals("INVALID_REPRESENTASJON", exception.message)
        assertNull(securityContextTargetPid()?.value)
    }

    private fun securityContextEnricher() =
        SecurityContextEnricher(
            tokenSuppliers = EgressTokenSuppliersByService(emptyMap()),
            securityContextPidExtractor,
            pidDecrypter,
            representasjonService
        )

    private companion object {
        private fun securityContextTargetPid() =
            SecurityContextHolder.getContext().authentication?.enriched()?.targetPid()
    }
}
