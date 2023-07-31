package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.avtale.AvtaleKategori

data class PensjonsavtaleDto(
    val produktbetegnelse: String,
    val kategori: AvtaleKategori,
    val startAlder: Int?, // NB: not mandatory
    val sluttAlder: Int?,
    val utbetalingsperioder: List<UtbetalingsperiodeDto>
)

data class UtbetalingsperiodeDto(
    val startAlder: Int,
    val startMaaned: Int,
    val sluttAlder: Int?,
    val sluttMaaned: Int?,
    val aarligUtbetaling: Int,
    val grad: Int
)
