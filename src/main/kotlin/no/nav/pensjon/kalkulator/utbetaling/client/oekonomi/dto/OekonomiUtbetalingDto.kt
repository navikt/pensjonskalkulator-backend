package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto

import java.math.BigDecimal
import java.time.LocalDate

data class OekonomiUtbetalingDto(
    val utbetalingsstatus: String,
    val posteringsdato: LocalDate,
    val forfallsdato: LocalDate?,
    val utbetalingsdato: LocalDate?,
    val utbetalingNettobeloep: BigDecimal?,
    val ytelseListe: List<Ytelse> = emptyList(),
)

data class Ytelse(
    val ytelsestype: String?,
    val ytelsesperiode: Periode,
    val ytelseskomponentersum: BigDecimal,

)

data class Periode(
    val fom: LocalDate,
    val tom: LocalDate,
)
