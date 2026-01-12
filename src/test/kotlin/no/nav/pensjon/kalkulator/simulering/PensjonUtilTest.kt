package no.nav.pensjon.kalkulator.simulering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate

class PensjonUtilTest : ShouldSpec({

    context("uttakDato") {
        should("be first in month after uttaksalder") {
            PensjonUtil.uttakDato(
                foedselDato = LocalDate.of(1963, 2, 1),
                uttakAlder = Alder(aar = 67, maaneder = 0)
            ) shouldBe LocalDate.of(2030, 3, 1)
        }

        should("include uttaksmåned in calculation") {
            PensjonUtil.uttakDato(
                foedselDato = LocalDate.of(1963, 3, 31),
                uttakAlder = Alder(aar = 67, maaneder = 11)
            ) shouldBe LocalDate.of(2031, 3, 1)
        }
    }
})
