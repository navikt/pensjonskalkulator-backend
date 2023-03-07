package no.nav.pensjon.kalkulator.opptjening

import java.math.BigDecimal

object InntektUtil {

    fun sistePensjonsgivendeInntekt(grunnlag: Opptjeningsgrunnlag): BigDecimal =
        grunnlag.inntekter
            .filter { it.type == Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT && it.beloep != BigDecimal.ZERO }
            .maxByOrNull { it.aar }
            ?.beloep
            ?: BigDecimal.ZERO
}
