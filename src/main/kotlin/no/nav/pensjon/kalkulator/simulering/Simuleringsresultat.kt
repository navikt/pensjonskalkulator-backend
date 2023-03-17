package no.nav.pensjon.kalkulator.simulering

import java.math.BigDecimal

data class Simuleringsresultat(val pensjonsaar: Int, val pensjonsbeloep: BigDecimal, val alder: Int)
