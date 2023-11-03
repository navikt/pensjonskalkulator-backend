package no.nav.pensjon.kalkulator.tech.security.ingress

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

class SecurityContextPidExtractorTest {

    @Test
    fun `pid returns PID if found in JWT 'pid' claim`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("pid")))
        assertEquals(pid, SecurityContextPidExtractor().pid())
    }

    @Test
    fun `pid returns null if no 'pid' claim in JWT`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("no-pid")))
        assertNull(SecurityContextPidExtractor().pid())
    }

    private class MockAuthentication(private val pidClaimKey: String) : Authentication {

        override fun getCredentials(): Any =
            Jwt("token1", Instant.MIN, Instant.MAX, mapOf("k" to "v"), mapOf(pidClaimKey to pid.value))

        override fun getName(): String = ""
        override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
        override fun getDetails(): Any = ""
        override fun getPrincipal(): Any = ""
        override fun isAuthenticated(): Boolean = false
        override fun setAuthenticated(isAuthenticated: Boolean) {}
    }
}
