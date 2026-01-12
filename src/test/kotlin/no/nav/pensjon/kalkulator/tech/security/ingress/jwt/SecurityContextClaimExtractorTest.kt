package no.nav.pensjon.kalkulator.tech.security.ingress.jwt

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.testutil.Arrange

class SecurityContextClaimExtractorTest : ShouldSpec({

    should("extract claim from JWT in security context") {
        Arrange.authentication(claimKey = "key1", claimValue = "value1")
        SecurityContextClaimExtractor.claim("key1") shouldBe "value1"
    }
})
