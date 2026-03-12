package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.fag

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.testutil.Arrange

class FagtilgangServiceTest : ShouldSpec({

    should("gi 'false' når den ansatte ikke er medlem av faggruppe") {
        Arrange.authentication(claimKey = "groups", claimValue = emptyList<String>())
        FagtilgangService().tilgangInnvilget() shouldBe false
    }

    should("gi 'true' når den ansatte er medlem av faggruppe") {
        Arrange.authentication(claimKey = "groups", claimValue = listOf("group1", "group2"))
        FagtilgangService().tilgangInnvilget() shouldBe true
    }
})
