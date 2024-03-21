package no.nav.pensjon.kalkulator.simulering.api.dto

data class SimuleringResultatV3(
    val alderspensjon: List<PensjonsberegningV3> = emptyList(),
    val afpPrivat: List<PensjonsberegningV3> = emptyList(),
    val vilkaarsproeving: VilkaarsproevingV3
)

data class PensjonsberegningV3(val alder: Int, val beloep: Int)

data class VilkaarsproevingV3(
    val vilkaarErOppfylt: Boolean,
    val alternativ: AlternativV3?
)

data class AlternativV3(
    val gradertUttaksalder: AlderV3?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: AlderV3
)

data class AlderV3(val aar: Int, val maaneder: Int)
