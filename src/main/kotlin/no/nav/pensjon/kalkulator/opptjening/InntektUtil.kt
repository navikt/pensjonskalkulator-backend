package no.nav.pensjon.kalkulator.opptjening

import java.math.BigDecimal

object InntektUtil {

    private val opptjeningstype = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT

    fun pensjonsgivendeInntekt(grunnlag: Opptjeningsgrunnlag, minimumAar: Int): Inntekt =
        grunnlag.inntekter
            .filter { it.type == opptjeningstype && it.aar >= minimumAar }
            .maxByOrNull { it.aar }
            ?: zeroInntekt(minimumAar)

    private fun zeroInntekt(aar: Int) = Inntekt(opptjeningstype, aar, BigDecimal.ZERO)
}
