package no.nav.pensjon.kalkulator.tech.toggle.api

import jakarta.validation.constraints.NotNull

data class EnablementDto(
    @field:NotNull val enabled: Boolean
)
