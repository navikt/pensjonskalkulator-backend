package no.nav.pensjon.kalkulator.simulering.client.pen.dto

data class PenSimuleringResultDto(
    val alderspensjon: List<PenPensjonDto>,
    val afpPrivat: List<PenPensjonDto>,
    val afpOffentliglivsvarig: List<PenPensjonAfpOffentligDto>,
    val vilkaarsproeving: PenVilkaarsproevingDto?
)

data class PenPensjonDto(
    val alder: Int,
    val beloep: Int
)

data class PenPensjonAfpOffentligDto(
    val alder: Int,
    val beloep: Int,
    val tpOrdning: String,
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
