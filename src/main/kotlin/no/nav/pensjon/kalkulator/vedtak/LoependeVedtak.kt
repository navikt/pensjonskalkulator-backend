package no.nav.pensjon.kalkulator.vedtak

import java.time.LocalDate

data class LoependeVedtak(
    val alderspensjon: LoependeVedtakDetaljer?,
    val ufoeretrygd: LoependeVedtakDetaljer?,
    val afpPrivat: LoependeVedtakDetaljer?,
    val afpOffentlig: LoependeVedtakDetaljer?,
    val afpOffentligForBrukereFoedtFoer1963: LoependeVedtakDetaljer? = null,
)

data class LoependeVedtakDetaljer(
    val grad: Int,
    val fom: LocalDate,
)
