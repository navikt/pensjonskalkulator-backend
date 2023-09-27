package no.nav.pensjon.kalkulator.avtale.api.dto

data class PensjonsavtalerDto(
    val avtaler: List<PensjonsavtaleDto>,
    val utilgjengeligeSelskap: List<SelskapDto>
)

data class PensjonsavtalerV0Dto(
    val avtaler: List<PensjonsavtaleV0Dto>,
    val utilgjengeligeSelskap: List<SelskapDto>
)
