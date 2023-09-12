package no.nav.pensjon.kalkulator.simulering.api.dto

data class SimuleringsresultatDto(
    val alderspensjon: List<PensjonsberegningDto> = emptyList(),
    val afpPrivat: List<PensjonsberegningDto> = emptyList(),
    val vilkaarErOppfylt: Boolean
)
