package no.nav.pensjon.kalkulator.vedtak.client.pen.dto

import java.time.LocalDate

data class PenLoependeVedtakDto(
    val alderspensjon: PenLopenedeVedtakMedGradDto?,
    val ufoeretrygd: PenLopenedeVedtakMedGradDto?,
    val afpPrivat: PenLopenedeVedtakMedGradDto?,
    val afpOffentlig: PenLopenedeVedtakMedGradDto?, //Afp i Offentlig Sektor for brukere født før 1963
)

data class PenLopenedeVedtakMedGradDto(
    val grad: Int,
    val fraOgMed: LocalDate,
)
