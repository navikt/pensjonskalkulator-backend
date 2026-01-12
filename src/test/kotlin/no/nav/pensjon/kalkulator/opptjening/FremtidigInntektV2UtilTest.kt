package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class FremtidigInntektV2UtilTest : ShouldSpec({

    context("pensjonsgivendeInntekt") {
        should("gi 0 hvis ingen inntekter") {
            val grunnlag = Opptjeningsgrunnlag(inntekter = emptyList())

            InntektUtil.pensjonsgivendeInntekt(grunnlag, minimumAar = 2020) shouldBe
                    Inntekt(
                        type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
                        aar = 2020,
                        beloep = BigDecimal.ZERO
                    )
        }

        should("gi 0 hvis ingen pensjonsgivende inntekter") {
            val grunnlag = Opptjeningsgrunnlag(
                inntekter = listOf(
                    Inntekt(
                        type = Opptjeningstype.OTHER, // ikke pensjonsgivende
                        aar = 2023,
                        beloep = BigDecimal.ONE
                    )
                )
            )

            InntektUtil.pensjonsgivendeInntekt(grunnlag, minimumAar = 2021) shouldBe
                    Inntekt(
                        type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
                        aar = 2021,
                        beloep = BigDecimal.ZERO
                    )
        }

        should("gi siste nylige inntekt") {
            val grunnlag = Opptjeningsgrunnlag(
                inntekter = listOf(
                    pensjonsgivendeInntekt(aar = 2021, beloep = BigDecimal.ONE),
                    pensjonsgivendeInntekt(aar = 2023, beloep = BigDecimal.TEN), // <--- siste
                    pensjonsgivendeInntekt(aar = 2022, beloep = BigDecimal("100"))
                )
            )

            InntektUtil.pensjonsgivendeInntekt(grunnlag, minimumAar = 2020) shouldBe
                    Inntekt(
                        type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
                        aar = 2023,
                        beloep = BigDecimal.TEN
                    )
        }

        should("gi siste nylige inntekt, selv om den er 0") {
            val grunnlag = Opptjeningsgrunnlag(
                inntekter = listOf(
                    pensjonsgivendeInntekt(aar = 2023, beloep = BigDecimal.ZERO), // <--- siste = 0
                    pensjonsgivendeInntekt(aar = 2022, beloep = BigDecimal.ONE),
                    pensjonsgivendeInntekt(aar = 2021, beloep = BigDecimal.TEN)
                )
            )

            InntektUtil.pensjonsgivendeInntekt(grunnlag, minimumAar = 2021) shouldBe
                    Inntekt(
                        type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
                        aar = 2023,
                        beloep = BigDecimal.ZERO
                    )
        }

        should("gi 0 hvis ingen nylige inntekter") {
            val grunnlag = Opptjeningsgrunnlag(
                inntekter = listOf(
                    pensjonsgivendeInntekt(aar = 1950, beloep = BigDecimal.ONE),
                    pensjonsgivendeInntekt(aar = 1951, beloep = BigDecimal.TEN)
                )
            )

            InntektUtil.pensjonsgivendeInntekt(grunnlag, minimumAar = 2020) shouldBe
                    Inntekt(
                        type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
                        aar = 2020,
                        beloep = BigDecimal.ZERO
                    )
        }
    }
})

private fun pensjonsgivendeInntekt(aar: Int, beloep: BigDecimal) =
    Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, aar, beloep)
