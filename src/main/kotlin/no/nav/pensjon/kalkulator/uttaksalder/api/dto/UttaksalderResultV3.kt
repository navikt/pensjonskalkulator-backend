package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import jakarta.validation.constraints.NotNull

data class UttaksalderResultV3(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
