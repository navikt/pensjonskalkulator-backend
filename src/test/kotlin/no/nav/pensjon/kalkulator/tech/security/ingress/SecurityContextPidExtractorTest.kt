package no.nav.pensjon.kalkulator.tech.security.ingress

import no.nav.pensjon.kalkulator.mock.MockAuthentication
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextPidExtractorTest {

    @Test
    fun `pid returns PID if found in JWT 'pid' claim`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("pid", pid.value)))
        assertEquals(pid, SecurityContextPidExtractor().pid())
    }

    @Test
    fun `pid returns null if no 'pid' claim in JWT`() {
        SecurityContextHolder.setContext(SecurityContextImpl(MockAuthentication("no-pid", pid.value)))
        assertNull(SecurityContextPidExtractor().pid())
    }
}
