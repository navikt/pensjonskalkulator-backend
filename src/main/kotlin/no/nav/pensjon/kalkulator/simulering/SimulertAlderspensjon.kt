package no.nav.pensjon.kalkulator.simulering

data class SimulertAlderspensjon(
    var alder: Int,
    val beloep: Int,
    val inntektspensjonBeloep: Int,
    val garantipensjonBeloep: Int,
    val delingstall: Double,
    val pensjonBeholdningFoerUttak: Int
)
