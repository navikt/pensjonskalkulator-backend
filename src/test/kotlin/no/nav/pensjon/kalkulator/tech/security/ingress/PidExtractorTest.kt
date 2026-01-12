package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class PidExtractorTest : ShouldSpec({

    should("throw informative RuntimeException if no PID in security context") {
        SecurityContextHolder.setContext(SecurityContextImpl(mockk()))
        shouldThrow<RuntimeException> { PidExtractor().pid() }.message shouldBe "No PID found"
    }
})
