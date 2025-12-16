package no.nav.pensjon.kalkulator.ansatt.api.dto

import jakarta.validation.constraints.NotNull

data class AnsattV1(
    @field:NotNull val id: String
)
