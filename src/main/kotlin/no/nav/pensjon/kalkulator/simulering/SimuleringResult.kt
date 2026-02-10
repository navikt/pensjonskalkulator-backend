package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.validity.Problem

data class SimuleringResult(
    val alderspensjon: List<SimulertAlderspensjon>,
    val alderspensjonMaanedsbeloep: AlderspensjonMaanedsbeloep? = null,
    val pre2025OffentligAfp: SimulertPre2025OffentligAfp? = null,
    val afpPrivat: List<SimulertAfpPrivat>,
    val afpOffentlig: List<SimulertAfpOffentlig>,
    val vilkaarsproeving: Vilkaarsproeving,
    val harForLiteTrygdetid: Boolean,
    val trygdetid: Int,
    val opptjeningGrunnlagListe: List<SimulertOpptjeningGrunnlag>,
    val problem: Problem? = null
)

data class SimulertOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

data class AlderspensjonMaanedsbeloep(
    val gradertUttak: Int?,
    val heltUttak: Int
)
