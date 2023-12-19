package no.nav.pensjon.kalkulator.opptjening

import java.math.BigDecimal

object InntektUtil {

    private val opptjeningstype = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT

    fun pensjonsgivendeInntekt(grunnlag: Opptjeningsgrunnlag, aar: Int): Inntekt =
        grunnlag.inntekter
            .filter { it.type == opptjeningstype }
            .firstOrNull { it.aar == aar }
            ?: zeroInntekt(aar)

    private fun zeroInntekt(aar: Int) = Inntekt(opptjeningstype, aar, BigDecimal.ZERO)
}
