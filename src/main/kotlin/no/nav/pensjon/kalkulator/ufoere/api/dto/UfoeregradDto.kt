package no.nav.pensjon.kalkulator.ufoere.api.dto

import jakarta.validation.constraints.NotNull

data class UfoeregradDto(
    @field:NotNull val ufoeregrad: Int
)