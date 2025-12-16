package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AnonymSimuleringResultV1(
    @field:NotNull val alderspensjon: List<AnonymPensjonsberegningV1> = emptyList(),
    val afpPrivat: List<AnonymPensjonsberegningV1>? = emptyList(),
    val afpOffentlig: List<AnonymPensjonsberegningAfpOffentligV1>? = emptyList(),
    @field:NotNull val vilkaarsproeving: AnonymVilkaarsproevingV1
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AnonymSimuleringErrorV1(
    @field:NotNull val status: String,
    @field:NotNull val message: String,
)

data class AnonymPensjonsberegningV1(@field:NotNull val alder: Int, @field:NotNull val beloep: Int)

data class AnonymPensjonsberegningAfpOffentligV1(@field:NotNull val alder: Int, @field:NotNull val beloep: Int)

data class AnonymVilkaarsproevingV1(
    @field:NotNull val vilkaarErOppfylt: Boolean,
    val alternativ: AnonymAlternativV1?
)

data class AnonymAlternativV1(
    val gradertUttaksalder: AnonymAlderV1?,
    val uttaksgrad: Int?, // null implies 100 %
    @field:NotNull val heltUttaksalder: AnonymAlderV1
)

data class AnonymAlderV1(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
