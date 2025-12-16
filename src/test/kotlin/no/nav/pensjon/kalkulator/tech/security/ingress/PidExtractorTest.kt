package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class PidExtractorTest : ShouldSpec( {

    should("throw informative RuntimeException if no PID in security context") {
        SecurityContextHolder.setContext(SecurityContextImpl(mockk()))
        val exception = assertThrows(RuntimeException::class.java) { PidExtractor().pid() }
        assertEquals("No PID found", exception.message)
    }
})
