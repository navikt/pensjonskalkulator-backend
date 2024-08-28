package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringResultatV6(
    val alderspensjon: List<PensjonsberegningV6> = emptyList(),
    val afpPrivat: List<PensjonsberegningV6>? = emptyList(),
    val afpOffentlig: List<PensjonsberegningAfpOffentligV6>? = emptyList(),
    val vilkaarsproeving: VilkaarsproevingV6,
    val harForLiteTrygdetid: Boolean
)

data class PensjonsberegningV6(val alder: Int, val beloep: Int)

data class PensjonsberegningAfpOffentligV6(val alder: Int, val beloep: Int)

data class VilkaarsproevingV6(
    val vilkaarErOppfylt: Boolean,
    val alternativ: AlternativV6?
)

data class AlternativV6(
    val gradertUttaksalder: AlderV6?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: AlderV6
)

data class AlderV6(val aar: Int, val maaneder: Int)
