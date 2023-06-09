package no.nav.pensjon.kalkulator.simulering.client.pen.dto

data class SimuleringResponseDto(
    val alderspensjon: List<SimulertAlderspensjonDto>,
    val afpPrivat: List<SimulertAfpPrivatDto>,
)
