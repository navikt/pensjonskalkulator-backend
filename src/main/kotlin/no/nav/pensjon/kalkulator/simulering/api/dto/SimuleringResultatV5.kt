package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringResultatV5(
    val alderspensjon: List<PensjonsberegningV5> = emptyList(),
    val afpPrivat: AfpPrivatV5? = null,
    val afpOffentlig: AfpOffentligV5? = null,
    val vilkaarsproeving: VilkaarsproevingV5
)

data class PensjonsberegningV5(val alder: Int, val beloep: Int)

data class AfpPrivatV5(val afpPrivatListe: List<PensjonsberegningV5>)

data class AfpOffentligV5(val afpLeverandoer: String, val afpOffentligListe: List<PensjonsberegningAfpOffentligV5>)

data class PensjonsberegningAfpOffentligV5(val alder: Int, val beloep: Int)

data class VilkaarsproevingV5(
    val vilkaarErOppfylt: Boolean,
    val alternativ: AlternativV5?
)

data class AlternativV5(
    val gradertUttaksalder: AlderV5?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: AlderV5
)

data class AlderV5(val aar: Int, val maaneder: Int)
