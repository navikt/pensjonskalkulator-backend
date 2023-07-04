package no.nav.pensjon.kalkulator.simulering.api.dto

data class SimuleringsresultatDto(
    val alderspensjon: List<PensjonsberegningDto>,
    val afpPrivat: List<PensjonsberegningDto>,
)
