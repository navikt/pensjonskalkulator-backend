package no.nav.pensjon.kalkulator.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class NavnTest : ShouldSpec({

    should("formatere navnet til standard format") {
        Navn(
            fornavn = "For",
            mellomnavn = "mellom",
            etternavn = "ETTER"
        ).formatert() shouldBe "For Mellom Etter"
    }
})