package no.nav.pensjon.kalkulator.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class NavnTest : ShouldSpec({

    should("formatere navnet til standard format") {
        with(
            Navn(
                fornavn = "for",
                mellomnavn = "Mellom",
                etternavn = "ETTER"
            )
        ) {
            formatert shouldBe Navn(fornavn = "For", mellomnavn = "Mellom", etternavn = "Etter", erFormatert = true)
            formatertStreng shouldBe "For Mellom Etter"
        }
    }

    should("håndtere flere fornavn") {
        with(
            Navn(
                fornavn = "for Navn",
                mellomnavn = "m.",
                etternavn = "etteR"
            )
        ) {
            formatert shouldBe Navn(fornavn = "For Navn", mellomnavn = "M.", etternavn = "Etter", erFormatert = true)
            formatertStreng shouldBe "For Navn M. Etter"
        }
    }

    should("håndtere manglende mellomnavn") {
        with(
            Navn(
                fornavn = "for Navn",
                mellomnavn = null,
                etternavn = "etteR-Navn"
            )
        ) {
            formatert shouldBe Navn(fornavn = "For Navn", mellomnavn = "", etternavn = "Etter-Navn", erFormatert = true)
            formatertStreng shouldBe "For Navn Etter-Navn"
        }
    }

    should("håndtere bindestrek") {
        with(
            Navn(
                fornavn = "for-Navn",
                mellomnavn = "mel-lom",
                etternavn = "etteR-navnet"
            )
        ) {
            formatert shouldBe Navn(fornavn = "For-Navn", mellomnavn = "Mel-Lom", etternavn = "Etter-Navnet", erFormatert = true)
            formatertStreng shouldBe "For-Navn Mel-Lom Etter-Navnet"
        }
    }
})