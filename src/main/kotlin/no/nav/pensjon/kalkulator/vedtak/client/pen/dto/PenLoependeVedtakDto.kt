package no.nav.pensjon.kalkulator.vedtak.client.pen.dto

import java.time.LocalDate

data class PenLoependeVedtakDto(
    val alderspensjon: PenGjeldendeVedtakApDto?,
    val alderspensjonIFremtid: PenGjeldendeVedtakApDto?,
    val ufoeretrygd: PenGjeldendeUfoeregradDto?,
    val afpPrivat: PenGjeldendeVedtakDto?,
    val afpOffentlig: PenGjeldendeVedtakDto?, //Afp i Offentlig Sektor for brukere født før 1963
)

data class PenGjeldendeUfoeregradDto(
    val grad: Int,
    val fraOgMed: LocalDate,
)

data class PenGjeldendeVedtakApDto(
    val grad: Int,
    val fraOgMed: LocalDate,
    val sivilstand: String,
)

data class PenGjeldendeVedtakDto(
    val fraOgMed: LocalDate,
)
