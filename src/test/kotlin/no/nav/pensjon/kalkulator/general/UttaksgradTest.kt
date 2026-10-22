package no.nav.pensjon.kalkulator.general

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class UttaksgradTest : ShouldSpec({

    context("from") {
        context("OK prosentsats") {
            should("mappe til intern domenerepresentasjon") {
                Uttaksgrad.from(prosentsats = 40) shouldBe Uttaksgrad.FOERTI_PROSENT
            }
        }

        context("ugyldig verdi for prosentsats") {
            should("gi feilmelding") {
                shouldThrow<IllegalArgumentException> {
                    Uttaksgrad.from(prosentsats = 10)
                }.message shouldBe "Ugyldig prosentsats for uttaksgrad 10"
            }
        }
    }
})