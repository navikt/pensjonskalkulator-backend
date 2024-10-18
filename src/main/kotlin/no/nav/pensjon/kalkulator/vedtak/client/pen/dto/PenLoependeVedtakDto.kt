package no.nav.pensjon.kalkulator.vedtak.client.pen.dto

import java.time.LocalDate

data class PenLoependeVedtakDto(
    val alderspensjon: PenGjeldendeVedtakMedGradDto?,
    val fremtidigLoependeVedtakAp: Boolean,
    val ufoeretrygd: PenGjeldendeVedtakMedGradDto?,
    val afpPrivat: PenGjeldendeVedtakDto?,
    val afpOffentlig: PenGjeldendeVedtakDto?, //Afp i Offentlig Sektor for brukere født før 1963
)

data class PenGjeldendeVedtakMedGradDto(
    val grad: Int,
    val fraOgMed: LocalDate,
)

data class PenGjeldendeVedtakDto(
    val fraOgMed: LocalDate,
)
