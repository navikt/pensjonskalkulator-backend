package no.nav.pensjon.kalkulator.opptjening

data class Opptjeningsgrunnlag(
    val inntekter: List<Inntekt>
) {
    companion object {
        fun empty() = Opptjeningsgrunnlag(inntekter = emptyList())
    }
}