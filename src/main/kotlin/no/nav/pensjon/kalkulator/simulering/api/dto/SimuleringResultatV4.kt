package no.nav.pensjon.kalkulator.simulering.api.dto

data class SimuleringResultatV4(
    val alderspensjon: List<PensjonsberegningV4> = emptyList(),
    val afpPrivat: List<PensjonsberegningV4> = emptyList(),
    val afpOffentlig: List<PensjonsberegningAfpOffentligV4> = emptyList(),
    val vilkaarsproeving: VilkaarsproevingV4
)

data class PensjonsberegningV4(val alder: Int, val beloep: Int)

data class PensjonsberegningAfpOffentligV4(val alder: Int, val beloep: Int, val afpLeverandoer: String)

data class VilkaarsproevingV4(
    val vilkaarErOppfylt: Boolean,
    val alternativ: AlternativV4?
)

data class AlternativV4(
    val gradertUttaksalder: AlderV4?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: AlderV4
)

data class AlderV4(val aar: Int, val maaneder: Int)
