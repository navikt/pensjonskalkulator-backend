package no.nav.pensjon.kalkulator.avtale

data class Pensjonsavtale(
    val produktbetegnelse: String,
    val kategori: String,
    val startAlder: Int,
    val sluttAlder: Int?,
    val utbetalingsperiode: Utbetalingsperiode
)
