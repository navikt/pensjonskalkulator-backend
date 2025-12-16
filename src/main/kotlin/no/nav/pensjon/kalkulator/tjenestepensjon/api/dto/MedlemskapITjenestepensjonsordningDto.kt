package no.nav.pensjon.kalkulator.tjenestepensjon.api.dto

import jakarta.validation.constraints.NotNull

data class MedlemskapITjenestepensjonsordningDto(
    @field:NotNull val tpLeverandoerListe: List<String>
)