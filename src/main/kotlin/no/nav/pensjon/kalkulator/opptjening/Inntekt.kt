package no.nav.pensjon.kalkulator.opptjening

import java.math.BigDecimal

data class Inntekt(val type: Opptjeningstype, val aar: Int, val beloep: BigDecimal)
