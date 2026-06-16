package no.nav.pensjon.kalkulator.opptjening.api.v1.acl

import jakarta.validation.constraints.NotNull

data class OpptjeningV1Result(
    @field:NotNull val opptjeningListe: List<OpptjeningV1>
)

data class OpptjeningV1(
    @field:NotNull val aar: Int,
    @field:NotNull val pensjonsgivendeInntekt: Int,
    @field:NotNull val pensjonspoeng: Double,
    val omsorgspoeng: Int?,
    @field:NotNull val beholdning: Int
)