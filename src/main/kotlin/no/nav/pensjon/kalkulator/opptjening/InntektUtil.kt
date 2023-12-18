package no.nav.pensjon.kalkulator.opptjening

import java.math.BigDecimal

object InntektUtil {

    fun sistePensjonsgivendeInntekt(grunnlag: Opptjeningsgrunnlag): Inntekt =
        grunnlag.inntekter
            .filter { it.type == Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT }
            .maxByOrNull { it.aar }
            ?: emptyInntekt()

    private fun emptyInntekt() = Inntekt(Opptjeningstype.OTHER, 0, BigDecimal.ZERO)
}
