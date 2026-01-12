package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.testutil.Arrange

class SecurityContextGroupServiceTest : ShouldSpec({

    should("extract groups from JWT in security context") {
        Arrange.authentication(claimKey = "groups", claimValue = listOf("group1", "group2"))

        val groups = SecurityContextGroupService().groups()

        groups shouldHaveSize 2
        groups[0] shouldBe "group1"
        groups[1] shouldBe "group2"
    }
})
