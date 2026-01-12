package no.nav.pensjon.kalkulator.general

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class AlderTest : ShouldSpec({

    context("constructor") {
        should("accept legal values") {
            Alder(aar = 0, maaneder = 0).maaneder shouldBe 0
            Alder(aar = 100, maaneder = 11).maaneder shouldBe 11
        }
    }

    context("lessThan") {
        should("return true if less than, otherwise false") {
            Alder(aar = 99, maaneder = 11) lessThan null shouldBe true
            Alder(aar = 2, maaneder = 11) lessThan Alder(aar = 3, maaneder = 0) shouldBe true
            Alder(aar = 1, maaneder = 5) lessThan Alder(aar = 1, maaneder = 5) shouldBe false
            Alder(aar = 3, maaneder = 0) lessThan Alder(aar = 2, maaneder = 11) shouldBe false
            Alder(aar = 4, maaneder = 10) lessThan Alder(aar = 4, maaneder = 9) shouldBe false
        }
    }

    context("lessThanOrEqualTo") {
        should("return true if less than or equal to, otherwise false") {
            Alder(aar = 99, maaneder = 11) lessThanOrEqualTo null shouldBe true
            Alder(aar = 2, maaneder = 11) lessThanOrEqualTo Alder(aar = 3, maaneder = 0) shouldBe true
            Alder(aar = 1, maaneder = 5) lessThanOrEqualTo Alder(aar = 1, maaneder = 5) shouldBe true
            Alder(aar = 3, maaneder = 0) lessThanOrEqualTo Alder(aar = 2, maaneder = 11) shouldBe false
            Alder(aar = 4, maaneder = 10) lessThanOrEqualTo Alder(aar = 4, maaneder = 9) shouldBe false
        }
    }

    context("minusAar") {
        should("subtract given number of måneder from alder") {
            Alder(aar = 99, maaneder = 11) minusAar 0 shouldBe Alder(aar = 99, maaneder = 11)
            Alder(aar = 20, maaneder = 1) minusAar 1 shouldBe Alder(aar = 19, maaneder = 1)
            Alder(aar = 3, maaneder = 5) minusAar 2 shouldBe Alder(aar = 1, maaneder = 5)
            Alder(aar = 3, maaneder = 0) minusAar 12 shouldBe Alder(aar = -9, maaneder = 0)
            Alder(aar = 4, maaneder = 10) minusAar -11 shouldBe Alder(aar = 15, maaneder = 10)
        }
    }

    context("plussMaaneder") {
        should("add given number of måneder to alder") {
            Alder(aar = 99, maaneder = 11) plussMaaneder 0 shouldBe Alder(aar = 99, maaneder = 11)
            Alder(aar = 2, maaneder = 11) plussMaaneder 1 shouldBe Alder(aar = 3, maaneder = 0)
            Alder(aar = 1, maaneder = 5) plussMaaneder 2 shouldBe Alder(aar = 1, maaneder = 7)
            Alder(aar = 3, maaneder = 0) plussMaaneder 12 shouldBe Alder(aar = 4, maaneder = 0)
            Alder(aar = 4, maaneder = 10) plussMaaneder -11 shouldBe Alder(aar = 3, maaneder = 11)
        }
    }

    context("from") {
        should("deduce alder from fødselsdato and dato") {
            /**
             * Ved lik månedsdag anses ikke måneden for helt fylt - dermed 1 måned fratrekk
             */
            Alder.from(
                foedselDato = LocalDate.of(2001, 1, 1),
                dato = LocalDate.of(2024, 1, 1)
            ) shouldBe Alder(aar = 22, maaneder = 11)

            Alder.from(
                foedselDato = LocalDate.of(1970, 2, 2),
                dato = LocalDate.of(2020, 2, 1)
            ) shouldBe Alder(aar = 49, maaneder = 11)

            Alder.from(
                foedselDato = LocalDate.of(2024, 1, 1),
                dato = LocalDate.of(2024, 12, 31)
            ) shouldBe Alder(aar = 0, maaneder = 11)
        }
    }

    context("illegal måneder values") {
        should("throw 'illegal argument' exception") {
            testIllegalMaanederValue(maaneder = -1)
            testIllegalMaanederValue(maaneder = 12)
        }
    }
})

private fun testIllegalMaanederValue(maaneder: Int) {
    shouldThrow<IllegalArgumentException> {
        Alder(aar = 29, maaneder)
    }.message shouldBe "0 <= maaneder <= 11"
}
