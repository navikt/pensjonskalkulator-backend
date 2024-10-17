package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto

import java.time.LocalDate

data class HentUtbetalingerRequestDto(
    val ident: String,
    val rolle: String,
    val periode: OekonomiYtelsesPeriodeDto,
    val periodetype: String,
)

data class OekonomiYtelsesPeriodeDto(
    val fom: LocalDate,
    val tom: LocalDate,
)