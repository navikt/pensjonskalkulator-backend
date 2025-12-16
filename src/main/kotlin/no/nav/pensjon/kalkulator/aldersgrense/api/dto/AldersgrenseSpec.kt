package no.nav.pensjon.kalkulator.aldersgrense.api.dto

import jakarta.validation.constraints.NotNull

data class AldersgrenseSpec(
    @field:NotNull val foedselsdato: Int
)
