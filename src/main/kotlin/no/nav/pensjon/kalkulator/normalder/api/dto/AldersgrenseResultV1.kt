package no.nav.pensjon.kalkulator.normalder.api.dto

import jakarta.validation.constraints.NotNull

data class AldersgrenseResultV1(
    @field:NotNull val normertPensjoneringsalder: PersonAlder,
    @field:NotNull val nedreAldersgrense: PersonAlder
)

data class PersonAlder(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)
