package no.nav.pensjon.kalkulator.ekskludering.api.dto

import jakarta.validation.constraints.NotNull

data class EkskluderingStatusV1(
    @field:NotNull val ekskludert: Boolean,
    @field:NotNull val aarsak: EkskluderingAarsakV1
)
