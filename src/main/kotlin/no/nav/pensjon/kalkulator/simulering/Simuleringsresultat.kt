package no.nav.pensjon.kalkulator.simulering

data class Simuleringsresultat(
    val alderspensjon: List<SimulertAlderspensjon>,
    val afpPrivat: List<SimulertAfpPrivat>,
)
