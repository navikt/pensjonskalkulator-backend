package no.nav.pensjon.kalkulator.ekskludering.api.dto

import jakarta.validation.constraints.NotNull

data class EkskluderingStatusV2(
    @field:NotNull val ekskludert: Boolean,
    @field:NotNull val aarsak: EkskluderingAarsakV2
)
