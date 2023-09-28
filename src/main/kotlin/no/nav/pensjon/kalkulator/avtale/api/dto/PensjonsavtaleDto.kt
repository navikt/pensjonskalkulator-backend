package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.avtale.AvtaleKategori
import no.nav.pensjon.kalkulator.general.Alder

data class PensjonsavtaleDto(
    val produktbetegnelse: String,
    val kategori: AvtaleKategori,
    val startAar: Int?, // år som i alder – NB: ikke påkrevd
    val sluttAar: Int?, // år som i alder
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
    val startAlder: Alder, // maaneder = 0..11
    val sluttAlder: Alder?, // maaneder = 0..11
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
