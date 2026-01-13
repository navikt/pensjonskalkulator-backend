package no.nav.pensjon.kalkulator.normalder.api.dto

import jakarta.validation.constraints.NotNull

data class AldersgrenseResultV2(
    @field:NotNull val normertPensjoneringsalder: PersonAlderV2,
    @field:NotNull val nedreAldersgrense: PersonAlderV2,
    @field:NotNull val oevreAldersgrense: PersonAlderV2,
)

data class PersonAlderV2(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
