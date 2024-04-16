package no.nav.pensjon.kalkulator.tech.security.ingress

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class PidExtractorTest {

    @Test
    fun `'pid' function throws informative RuntimeException if no PID in security context`() {
        SecurityContextHolder.setContext(SecurityContextImpl(null))
        val exception = assertThrows(RuntimeException::class.java) { PidExtractor().pid() }
        assertEquals("No PID found", exception.message)
    }
}
