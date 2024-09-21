package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringResultatV6(
    val alderspensjon: List<AlderspensjonsberegningV6> = emptyList(),
    val afpPrivat: List<PensjonsberegningV6>? = emptyList(),
    val afpOffentlig: List<PensjonsberegningAfpOffentligV6>? = emptyList(),
    val vilkaarsproeving: VilkaarsproevingV6,
    val harForLiteTrygdetid: Boolean? = false,
    val trygdetid: Int? = 0,
    val opptjeningGrunnlagListe: List<SimulertOpptjeningGrunnlagV6>? = emptyList()
)

data class PensjonsberegningV6(val alder: Int, val beloep: Int)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlderspensjonsberegningV6(
    val alder: Int,
    val beloep: Int,
    val inntektspensjonBeloep: Int? = 0,
    val garantipensjonBeloep: Int? = 0,
    val delingstall: Double? = 0.0,
    val pensjonBeholdningFoerUttakBeloep: Int? = 0
)

data class PensjonsberegningAfpOffentligV6(val alder: Int, val beloep: Int)

data class SimulertOpptjeningGrunnlagV6(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VilkaarsproevingV6(
    val vilkaarErOppfylt: Boolean,
    val alternativ: AlternativV6?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlternativV6(
    val gradertUttaksalder: AlderV6?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: AlderV6
)

data class AlderV6(val aar: Int, val maaneder: Int)
