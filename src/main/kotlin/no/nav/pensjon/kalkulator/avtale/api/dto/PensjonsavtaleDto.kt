package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.general.Alder

data class PensjonsavtaleDto(
    val produktbetegnelse: String,
    val kategori: AvtaleKategori,
    val startAlder: Int?, // NB: not mandatory
    val sluttAlder: Int?,
    val utbetalingsperioder: List<UtbetalingsperiodeDto>
)

data class PensjonsavtaleV0Dto(
    val produktbetegnelse: String,
    val kategori: AvtaleKategori,
    val startAlder: Int?, // NB: not mandatory
    val sluttAlder: Int?,
    val utbetalingsperioder: List<UtbetalingsperiodeV0Dto>
)

data class UtbetalingsperiodeDto(
    val start: Alder, // maaneder = 0..11
    val slutt: Alder?, // maaneder = 0..11
    val aarligUtbetaling: Int,
    val grad: Int
)

data class UtbetalingsperiodeV0Dto(
    val startAlder: Int,
    val startMaaned: Int, // 1..12
    val sluttAlder: Int?,
    val sluttMaaned: Int?, // 1..12
    val aarligUtbetaling: Int,
    val grad: Int
)
