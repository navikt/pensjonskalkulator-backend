package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.validity.Problem

data class SimuleringResult(
    val alderspensjon: List<SimulertAlderspensjon>,
    val alderspensjonMaanedsbeloep: AlderspensjonMaanedsbeloep? = null,
    val maanedligAlderspensjonForKnekkpunkter: SimulertMaanedligAlderspensjonForKnekkpunkter? = null,
    val pre2025OffentligAfp: SimulertPre2025OffentligAfp? = null,
    val afpPrivat: List<SimulertAfpPrivat>,
    val afpOffentlig: List<SimulertAfpOffentlig>,
    val vilkaarsproeving: Vilkaarsproeving,
    val harForLiteTrygdetid: Boolean,
    val trygdetid: Int,
    val opptjeningGrunnlagListe: List<SimulertOpptjeningGrunnlag>,
    val alderAar: Int? = null,
    val problem: Problem? = null
) {
    fun withAlderAar(alderAar: Int) =
        copy(alderAar = alderAar)
}

data class SimulertOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

data class AlderspensjonMaanedsbeloep(
    val gradertUttak: Int?,
    val heltUttak: Int
)

data class SimulertMaanedligAlderspensjonForKnekkpunkter(
    val vedGradertUttak: SimulertMaanedligAlderspensjon?,
    val vedHeltUttak: SimulertMaanedligAlderspensjon,
    val vedNormertPensjonsalder: SimulertMaanedligAlderspensjon
)

data class SimulertMaanedligAlderspensjon(
    val beloep: Int,
    val inntektspensjonBeloep: Int?,
    val delingstall: Double?,
    val pensjonsbeholdningFoerUttak: Int?,
    val pensjonsbeholdningEtterUttak: Int?,
    val sluttpoengtall: Double?,
    val poengaarFoer92: Int?,
    val poengaarEtter91: Int?,
    val forholdstall: Double?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val pensjonstillegg: Int?,
    val skjermingstillegg: Int?,
    val kapittel19Pensjon: Kapittel19Pensjon?,
    val kapittel20Pensjon: Kapittel20Pensjon?
)
