package no.nav.pensjon.kalkulator.opptjening

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class InntektUtilTest {

    @Test
    fun `pensjonsgivendeInntekt returns zero when no inntekter`() {
        val grunnlag = Opptjeningsgrunnlag(emptyList())

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2023)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2023, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt returns zero when no pensjonsgivende inntekter`() {
        val grunnlag = Opptjeningsgrunnlag(listOf(Inntekt(Opptjeningstype.OTHER, 2023, BigDecimal.ONE)))

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2023)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2023, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt returns pensjonsgivende inntekt for angitt aar`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(2021, BigDecimal.ONE),
                pensjonsgivendeInntekt(2023, BigDecimal.TEN), // <--- angitt år
                pensjonsgivendeInntekt(2022, BigDecimal("100"))
            )
        )

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2023)

        assertEquals(BigDecimal.TEN, inntekt.beloep)
        assertEquals(2023, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt returns pensjonsgivende inntekt for angitt aar, even if zero`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(2023, BigDecimal.ONE),
                pensjonsgivendeInntekt(2022, BigDecimal.ZERO), // <--- angitt år
                pensjonsgivendeInntekt(2021, BigDecimal.TEN)
            )
        )

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2022)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2022, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt returns zero inntekt when no inntekt exists for angitt aar`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(1950, BigDecimal.ONE),
                pensjonsgivendeInntekt(1951, BigDecimal.TEN)
            )
        )

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2022)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2022, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    companion object {
        private fun pensjonsgivendeInntekt(aar: Int, beloep: BigDecimal) =
            Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, aar, beloep)
    }
}
