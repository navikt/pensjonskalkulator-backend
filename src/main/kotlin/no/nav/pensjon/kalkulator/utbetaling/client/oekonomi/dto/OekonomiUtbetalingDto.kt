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
//    val ytelseNettobeloep: BigDecimal,
//    val skattsum: BigDecimal,
//    val trekksum: BigDecimal,
    val ytelseskomponentersum: BigDecimal,

//    val skattListe: List<Skatt>? = null,
//    val trekkListe: List<Trekk>? = null,
//    val ytelseskomponentListe: List<Ytelseskomponent>? = null,
)

data class Ytelseskomponent(
    val ytelseskomponenttype: String?,
    val satsbeloep: BigDecimal?,
    val satstype: String?,
    val satsantall: Double?,
    val ytelseskomponentbeloep: BigDecimal?,
)

data class Periode(
    val fom: LocalDate,
    val tom: LocalDate,
)
