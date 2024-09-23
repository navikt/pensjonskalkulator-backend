package no.nav.pensjon.kalkulator.vedtak.api.dto

data class LoependeVedtakDto(
    val alderspensjon: LoependeVedtakDetaljerDto?,
    val ufoeretrygd: LoependeVedtakDetaljerDto?,
    val afpPrivat: LoependeVedtakDetaljerDto?,
    val afpOffentlig: LoependeVedtakDetaljerDto?,
)

data class LoependeVedtakDetaljerDto(
    val loepende: Boolean = false,
    val grad: Int = 100
)
