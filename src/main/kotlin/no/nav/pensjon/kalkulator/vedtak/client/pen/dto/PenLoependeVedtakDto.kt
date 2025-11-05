package no.nav.pensjon.kalkulator.vedtak.client.pen.dto

import java.time.LocalDate

data class PenLoependeVedtakDto(
    val alderspensjon: PenGjeldendeVedtakApDto?,
    val alderspensjonIFremtid: PenGjeldendeVedtakApDto?,
    val ufoeretrygd: PenGjeldendeUfoeregradDto?,
    val afpPrivat: PenGjeldendeVedtakDto?,
    val afpOffentlig: PenGjeldendeVedtakDto?, // AFP i offentlig sektor for brukere født før 1963
    val gjeldendeUttaksgradFom: LocalDate? = null
)

data class PenGjeldendeUfoeregradDto(
    val grad: Int,
    val fraOgMed: LocalDate,
)

data class PenGjeldendeVedtakApDto(
    val grad: Int,
    val fraOgMed: LocalDate,
    val sivilstatus: String,
)

data class PenGjeldendeVedtakDto(
    val fraOgMed: LocalDate,
)
