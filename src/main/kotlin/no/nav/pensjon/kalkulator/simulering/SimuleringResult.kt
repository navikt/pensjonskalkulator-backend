package no.nav.pensjon.kalkulator.simulering

data class SimuleringResult(
    val alderspensjon: List<SimulertAlderspensjon>,
    val alderspensjonMaanedsbeloep: AlderspensjonMaanedsbeloep? = null,
    val afpPrivat: List<SimulertAfpPrivat>,
    val afpOffentlig: List<SimulertAfpOffentlig>,
    val vilkaarsproeving: Vilkaarsproeving,
    val harForLiteTrygdetid: Boolean,
    val trygdetid: Int,
    val opptjeningGrunnlagListe: List<SimulertOpptjeningGrunnlag>
)

data class SimulertOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

data class AlderspensjonMaanedsbeloep(
    val gradertUttak: Int?,
    val heltUttak: Int
)
