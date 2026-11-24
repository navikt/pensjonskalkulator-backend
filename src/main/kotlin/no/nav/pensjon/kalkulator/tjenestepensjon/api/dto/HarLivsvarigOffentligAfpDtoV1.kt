package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto

import jakarta.validation.constraints.NotNull

data class HarLivsvarigOffentligAfpDtoV1 (
    @field:NotNull val harLivsvarigOffentligAfp: Boolean
)
