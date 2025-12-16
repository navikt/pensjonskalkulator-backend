package no.nav.pensjon.kalkulator.ekskludering.api.dto

import jakarta.validation.constraints.NotNull

//TODO: Should not mix version numbers (ApotekerStatusV1 vs EkskluderingAarsakV2)
data class ApotekerStatusV1(
    @field:NotNull val apoteker: Boolean,
    @field:NotNull val aarsak: EkskluderingAarsakV2
)