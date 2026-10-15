package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag

data class PoppOpptjeningsgrunnlag(
    val inntektListe: List<PoppInntekt>? = null
) {
    fun toInternalValue() =
        Opptjeningsgrunnlag(
            inntekter = inntektListe.orEmpty().map { it.toInternalValue() }
        )
}