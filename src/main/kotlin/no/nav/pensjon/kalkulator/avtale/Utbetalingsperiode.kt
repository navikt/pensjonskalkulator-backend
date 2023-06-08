package no.nav.pensjon.kalkulator.avtale

data class Utbetalingsperiode(
    val start: Alder,
    val slutt: Alder?,
    val aarligUtbetaling: Int,
    val grad: Int)
