package no.nav.pensjon.kalkulator.simulering.client.pen.dto

// Corresponds to SimuleringsresultatAlderspensjon1963Plus in PEN
data class PenSimuleringResultDto(
    val alderspensjon: List<PenPensjonDto>,
    val afpPrivat: List<PenPensjonDto>,
    val afpOffentliglivsvarig: List<PenPensjonAfpOffentligDto>,
    val vilkaarsproeving: PenVilkaarsproevingDto?,
    val harNokTrygdetidForGarantipensjon: Boolean?,
    val opptjeningGrunnlagListe: List<PenOpptjeningGrunnlag>?
)

data class PenPensjonDto(
    val alder: Int,
    val beloep: Int,
    val inntektspensjon: Int?,
    val garantipensjon: Int?,
    val delingstall: Double?,
    val pensjonBeholdningFoerUttak: Int?
)

data class PenPensjonAfpOffentligDto(
    val alder: Int,
    val beloep: Int
)

data class PenVilkaarsproevingDto(
    val vilkaarErOppfylt: Boolean,
    val alternativ: PenAlternativDto?
)

data class PenAlternativDto(
    val gradertUttaksalder: PenAlderDto?,
    val uttaksgrad: Int?,
    val heltUttaksalder: PenAlderDto
)

data class PenAlderDto(
    val aar: Int,
    val maaneder: Int
)

data class PenOpptjeningGrunnlag(
    val aar: Int,
    val pensjonsgivendeInntekt: Int
)
