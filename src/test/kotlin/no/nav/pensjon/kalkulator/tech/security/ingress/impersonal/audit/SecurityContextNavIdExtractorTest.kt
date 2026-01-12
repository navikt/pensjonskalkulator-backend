package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.testutil.Arrange

class SecurityContextNavIdExtractorTest : ShouldSpec({

    should("id returns Nav identifier if found in JWT 'NAVident' claim") {
        Arrange.authentication(claimKey = "NAVident", claimValue = "X123456")
        SecurityContextNavIdExtractor().id() shouldBe "X123456"
    }

    should("id returns empty string if no 'NAVident' claim in JWT") {
        Arrange.authentication(claimKey = "other-ident", claimValue = "id1")
        SecurityContextNavIdExtractor().id() shouldBe ""
    }
})
