package no.nav.pensjon.kalkulator.opptjening.api.dto

import jakarta.validation.constraints.NotNull

data class InntektDto(
    @field:NotNull val beloep: Int,
    @field:NotNull val aar: Int
)
