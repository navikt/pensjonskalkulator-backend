package no.nav.pensjon.kalkulator.opptjening

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class InntektUtilTest {

    @Test
    fun `sistePensjonsgivendeInntekt returns zero when no inntekter`() {
        val grunnlag = Opptjeningsgrunnlag(emptyList())

        val inntekt = InntektUtil.sistePensjonsgivendeInntekt(grunnlag)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(0, inntekt.aar)
    }

    @Test
    fun `sistePensjonsgivendeInntekt returns zero when no pensjonsgivende inntekter`() {
        val grunnlag = Opptjeningsgrunnlag(listOf(Inntekt(Opptjeningstype.OTHER, 2023, BigDecimal.ONE)))

        val inntekt = InntektUtil.sistePensjonsgivendeInntekt(grunnlag)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(0, inntekt.aar)
    }

    @Test
    fun `sistePensjonsgivendeInntekt returns siste pensjonsgivende inntekt`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(2021, BigDecimal.ONE),
                pensjonsgivendeInntekt(2023, BigDecimal.TEN), // <--- siste
                pensjonsgivendeInntekt(2022, BigDecimal("100"))
            )
        )

        val inntekt = InntektUtil.sistePensjonsgivendeInntekt(grunnlag)

        assertEquals(BigDecimal.TEN, inntekt.beloep)
        assertEquals(2023, inntekt.aar)
    }

    @Test
    fun `sistePensjonsgivendeInntekt returns siste pensjonsgivende inntekt, even if zero`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(2022, BigDecimal.ONE),
                pensjonsgivendeInntekt(2023, BigDecimal.ZERO), // <--- siste
                pensjonsgivendeInntekt(2021, BigDecimal.TEN)
            )
        )

        val inntekt = InntektUtil.sistePensjonsgivendeInntekt(grunnlag)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2023, inntekt.aar)
    }

    companion object {
        private fun pensjonsgivendeInntekt(aar: Int, beloep: BigDecimal) =
            Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, aar, beloep)
    }
}
