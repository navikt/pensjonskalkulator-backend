package no.nav.pensjon.kalkulator.general

import java.time.LocalDate

/**
 * Løpende inntekt, dvs. et beløp uten angitt sluttdato.
 */
data class LoependeInntekt(
    val fom: LocalDate, // startdato (fra og med)
    val beloep: Int
)
