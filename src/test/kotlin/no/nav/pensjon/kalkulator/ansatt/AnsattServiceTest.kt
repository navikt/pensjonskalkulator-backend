package no.nav.pensjon.kalkulator.ansatt

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor

class AnsattServiceTest : ShouldSpec({

    should("return ansatt-ID from security context") {
        AnsattService(
            ansattIdExtractor = mockk<SecurityContextNavIdExtractor>().apply { every { id() } returns "id1" }
        ).getAnsattId() shouldBe "id1"
    }
})
