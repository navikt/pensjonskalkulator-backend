package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class OpptjeningstypeTest : ShouldSpec({

    context("forCode") {
        should("return 'Sum pensjonsgivende inntekt' when code is 'SUM_PI'") {
            Opptjeningstype.forCode("SUM_PI") shouldBe Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT
        }

        should("return 'Other' when code is unknown") {
            Opptjeningstype.forCode("unknown") shouldBe Opptjeningstype.OTHER
        }
    }
})
