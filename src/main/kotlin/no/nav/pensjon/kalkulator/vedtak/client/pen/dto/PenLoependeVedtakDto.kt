package no.nav.pensjon.kalkulator.vedtak.client.pen.dto

data class PenLoependeVedtakDto(
    val alderspensjon: PenLopenedeVedtakMedGradDto?,
    val ufoeretrygd: PenLopenedeVedtakMedGradDto?,
    val afpPrivat: PenLopenedeVedtakMedGradDto?,
    val afpOffentlig: PenLopenedeVedtakMedGradDto?,
)

data class PenLopenedeVedtakMedGradDto(
    val grad: Int,
)
