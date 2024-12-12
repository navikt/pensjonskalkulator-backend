package no.nav.pensjon.kalkulator.opptjening

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class FremtidigInntektV2UtilTest {

    @Test
    fun `pensjonsgivendeInntekt gir 0 hvis ingen inntekter`() {
        val grunnlag = Opptjeningsgrunnlag(emptyList())

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2020)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2020, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt gir 0 hvis ingen pensjonsgivende inntekter`() {
        val grunnlag = Opptjeningsgrunnlag(listOf(Inntekt(Opptjeningstype.OTHER, 2023, BigDecimal.ONE)))

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2021)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2021, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt gir siste nylige inntekt`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(2021, BigDecimal.ONE),
                pensjonsgivendeInntekt(2023, BigDecimal.TEN), // <--- siste
                pensjonsgivendeInntekt(2022, BigDecimal("100"))
            )
        )

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2020)

        assertEquals(BigDecimal.TEN, inntekt.beloep)
        assertEquals(2023, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt gir siste nylige inntekt, selv om den er 0`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(2023, BigDecimal.ZERO), // <--- siste = 0
                pensjonsgivendeInntekt(2022, BigDecimal.ONE),
                pensjonsgivendeInntekt(2021, BigDecimal.TEN)
            )
        )

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2021)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2023, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    @Test
    fun `pensjonsgivendeInntekt gir 0 hvis ingen nylige inntekter`() {
        val grunnlag = Opptjeningsgrunnlag(
            listOf(
                pensjonsgivendeInntekt(1950, BigDecimal.ONE),
                pensjonsgivendeInntekt(1951, BigDecimal.TEN)
            )
        )

        val inntekt = InntektUtil.pensjonsgivendeInntekt(grunnlag, 2020)

        assertEquals(BigDecimal.ZERO, inntekt.beloep)
        assertEquals(2020, inntekt.aar)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
    }

    companion object {
        private fun pensjonsgivendeInntekt(aar: Int, beloep: BigDecimal) =
            Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, aar, beloep)
    }
}
