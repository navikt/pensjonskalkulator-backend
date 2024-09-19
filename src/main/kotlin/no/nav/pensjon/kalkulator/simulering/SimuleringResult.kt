package no.nav.pensjon.kalkulator.simulering

data class SimuleringResult(
    val alderspensjon: List<SimulertAlderspensjon>,
    val afpPrivat: List<SimulertAfpPrivat>,
    val afpOffentlig: List<SimulertAfpOffentlig>,
    val vilkaarsproeving: Vilkaarsproeving,
    val harForLiteTrygdetid: Boolean,
    val opptjeningGrunnlagListe: List<SimulertOpptjeningGrunnlag>
)

data class SimulertOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)
