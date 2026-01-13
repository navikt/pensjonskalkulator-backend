package no.nav.pensjon.kalkulator.normalder.api.dto

import jakarta.validation.constraints.NotNull

/**
 * Data transfer object used for both V1 and V2 of the API.
 */
data class AldersgrenseSpec(
    @field:NotNull val foedselsdato: Int
)
