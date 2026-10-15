package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import java.math.BigDecimal

class PoppOpptjeningsgrunnlagTest : ShouldSpec({

    should("map data transfer object to domain object") {
        PoppOpptjeningsgrunnlag(
            inntektListe = listOf(
                PoppInntekt(
                    inntektType = "SUM_PI",
                    inntektAr = 2023,
                    belop = 123L
                )
            )
        ).toInternalValue() shouldBe Opptjeningsgrunnlag(
            inntekter = listOf(
                Inntekt(
                    type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
                    aar = 2023,
                    beloep = BigDecimal(123)
                )
            )
        )
    }
})