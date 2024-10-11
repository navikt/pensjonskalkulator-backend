package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto

import java.time.LocalDate

data class HentUtbetalingerRequestDto(
    val ident: String,
    val rolle: String = "UTBETALT_TIL",
    val periode: OekonomiUtbetalingPeriodeDto = OekonomiUtbetalingPeriodeDto(),
    val periodetype: String = "UTBETALINGSPERIODE")


data class OekonomiUtbetalingPeriodeDto(val fom: LocalDate = LocalDate.now().minusMonths(1), val tom: LocalDate = LocalDate.now())