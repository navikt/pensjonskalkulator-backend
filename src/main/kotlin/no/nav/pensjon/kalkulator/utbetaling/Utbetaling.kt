package no.nav.pensjon.kalkulator.utbetaling

import java.math.BigDecimal
import java.time.LocalDate

data class Utbetaling(
    val utbetalingsdato: LocalDate?,
    val posteringsdato: LocalDate,
    val beloep: BigDecimal?,
    val erUtbetalt: Boolean,
    val gjelderAlderspensjon: Boolean,
    val fom: LocalDate,
    val tom: LocalDate,
)