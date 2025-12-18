package no.nav.pensjon.kalkulator.sak.api.dto

import jakarta.validation.constraints.NotNull

data class SakDto(
    @field:NotNull val harUfoeretrygdEllerGjenlevendeytelse: Boolean
)
