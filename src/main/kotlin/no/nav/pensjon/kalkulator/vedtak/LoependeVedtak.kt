package no.nav.pensjon.kalkulator.vedtak

import java.time.LocalDate

data class LoependeVedtak(
    val alderspensjon: LopenedeVedtakDetaljer?,
    val ufoeretrygd: LopenedeVedtakDetaljer?,
    val afpPrivat: LopenedeVedtakDetaljer?,
    val afpOffentlig: LopenedeVedtakDetaljer?,
)

data class LopenedeVedtakDetaljer(
    val grad: Int,
    val fom: LocalDate,
)
