package no.nav.pensjon.kalkulator.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

/**
 * Alle fødselsnumre og D-numre brukt her er syntetiske/fiktive.
 */
class PidTest : ShouldSpec({

    context("value") {
        should("return 'invalid' for invalid value") {
            Pid("bad value").value shouldBe "invalid"
        }

        should("return PID value for valid value") {
            Pid("04925398980").value shouldBe "04925398980"
        }
    }

    context("displayValue") {
        should("return 'invalid' for invalid value") {
            Pid("bad value").displayValue shouldBe "invalid"
        }

        should("return redacted value for valid value") {
            Pid("04925398980").displayValue shouldBe "049253*****"
        }
    }

    context("toString") {
        should("return 'invalid' for invalid value") {
            Pid("bad value").toString() shouldBe "invalid"
        }

        should("return redacted value for valid value") {
            Pid("04925398980").toString() shouldBe "049253*****"
        }
    }

    context("equals") {
        should("be true when string values are equal") {
            (Pid("04925398980") == Pid("04925398980")) shouldBe true
        }

        should("be false when string values are not equal") {
            (Pid("04925398980") == Pid("12906498357")) shouldBe false
        }

        should("be false when values are not both PID") {
            (Pid("04925398980").equals("04925398980")) shouldBe false
        }

        should("be false when value is null") {
            Pid("04925398980").equals(null) shouldBe false
        }
    }

    context("dato") {
        /**
         * Fødselsnummer fra Test-Norge har +80 i månedsverdi.
         */
        should("gi datodel som LocalDate for fødselsnummer fra Test-Norge") {
            Pid("04925398980").dato() shouldBe LocalDate.of(1953, 12, 4)
        }

        /**
         * D-nummer har +40 i dagsverdi.
         */
        should("gi datodel som LocalDate for D-nummer") {
            Pid("41018512345").dato() shouldBe LocalDate.of(1985, 1, 1)
        }

        /**
         * Dolly-nummer har +40 i månedsverdi.
         */
        should("gi datodel som LocalDate for Dolly-nummer") {
            Pid("01416637578").dato() shouldBe LocalDate.of(1966, 1, 1)
        }

        should("gi 1901-01-01 hvis ugyldig PID") {
            Pid("0492539898").dato() shouldBe LocalDate.of(1901, 1, 1)
        }

        should("gi 1902-02-02 hvis ugyldig datodel") {
            Pid("99416637578").dato() shouldBe LocalDate.of(1902, 2, 2)
        }
    }
})
