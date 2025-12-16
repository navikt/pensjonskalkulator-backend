package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto

import jakarta.validation.constraints.NotNull

data class TjenestepensjonsforholdDto(
    @field:NotNull val harTjenestepensjonsforhold: Boolean
)
