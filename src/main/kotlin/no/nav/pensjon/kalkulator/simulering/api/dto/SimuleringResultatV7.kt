package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringResultatV7(
    val alderspensjon: List<AlderspensjonsberegningV7> = emptyList(),
    val alderspensjonMaanedligVedEndring: AlderspensjonsMaanedligBeregningerV7? = null,
    val afpPrivat: List<PensjonsberegningV7>? = emptyList(),
    val afpOffentlig: List<PensjonsberegningAfpOffentligV7>? = emptyList(),
    val vilkaarsproeving: VilkaarsproevingV7,
    val harForLiteTrygdetid: Boolean? = false,
    val trygdetid: Int? = null,
    val opptjeningGrunnlagListe: List<SimulertOpptjeningGrunnlagV7>? = null
)

data class PensjonsberegningV7(val alder: Int, val beloep: Int)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlderspensjonsberegningV7(
    val alder: Int,
    val beloep: Int,
    val inntektspensjonBeloep: Int? = null,
    val garantipensjonBeloep: Int? = null,
    val delingstall: Double? = null,
    val pensjonBeholdningFoerUttakBeloep: Int? = null
)

data class PensjonsberegningAfpOffentligV7(val alder: Int, val beloep: Int)

data class SimulertOpptjeningGrunnlagV7(
    val aar: Int,
    val pensjonsgivendeInntektBeloep: Int
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VilkaarsproevingV7(
    val vilkaarErOppfylt: Boolean,
    val alternativ: AlternativV7?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlternativV7(
    val gradertUttaksalder: AlderV7?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: AlderV7
)

data class AlderV7(val aar: Int, val maaneder: Int)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlderspensjonsMaanedligBeregningerV7(
    val gradertUttakMaanedligBeloep: Int? = null,
    val heltUttakMaanedligBeloep: Int,
)
