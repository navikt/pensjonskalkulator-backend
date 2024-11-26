package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AnonymSimuleringResultV1(
    val alderspensjon: List<AnonymPensjonsberegningV1> = emptyList(),
    val afpPrivat: List<AnonymPensjonsberegningV1>? = emptyList(),
    val afpOffentlig: List<AnonymPensjonsberegningAfpOffentligV1>? = emptyList(),
    val vilkaarsproeving: AnonymVilkaarsproevingV1
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AnonymSimuleringErrorV1(
    val status: String,
    val message: String,
)

data class AnonymPensjonsberegningV1(val alder: Int, val beloep: Int)

data class AnonymPensjonsberegningAfpOffentligV1(val alder: Int, val beloep: Int)

data class AnonymVilkaarsproevingV1(
    val vilkaarErOppfylt: Boolean,
    val alternativ: AnonymAlternativV1?
)

data class AnonymAlternativV1(
    val gradertUttaksalder: AnonymAlderV1?,
    val uttaksgrad: Int?, // null implies 100 %
    val heltUttaksalder: AnonymAlderV1
)

data class AnonymAlderV1(
    val aar: Int,
    val maaneder: Int
)
