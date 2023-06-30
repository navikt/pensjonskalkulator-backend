package no.nav.pensjon.kalkulator.avtale.api.dto

data class PensjonsavtaleDto(
    val produktbetegnelse: String,
    val kategori: String,
    val startAlder: Int,
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
