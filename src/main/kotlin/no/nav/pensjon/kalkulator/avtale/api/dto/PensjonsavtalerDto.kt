package no.nav.pensjon.kalkulator.avtale.api.dto

data class PensjonsavtalerDto(
    val avtaler: List<PensjonsavtaleDto>,
    val utilgjengeligeSelskap: List<SelskapDto>
)
