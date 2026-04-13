package no.nav.pensjon.kalkulator.vedtak.client.pen.dto

import java.time.LocalDate

/**
 * Corresponds to no.nav.pensjon.pen.domain.api.vedtak.LoependeVedtak in PEN.
 */
data class PenLoependeVedtakDto(
    val alderspensjon: PenGjeldendeVedtakApDto?,
    val alderspensjonIFremtid: PenGjeldendeVedtakApDto?,
    val ufoeretrygd: PenGjeldendeUfoeregradDto?,
    val afpPrivat: PenGjeldendeVedtakDto?,
    val afpOffentlig: PenGjeldendeVedtakDto?, // AFP i offentlig sektor for brukere født før 1963
    val gjeldendeUttaksgradFom: LocalDate? = null,
    var avdoed: PenInformasjonOmAvdoedDto?
)

data class PenGjeldendeUfoeregradDto(
    val grad: Int,
    val fraOgMed: LocalDate
)

data class PenGjeldendeVedtakApDto(
    val grad: Int,
    val fraOgMed: LocalDate,
    val sivilstatus: String
)

data class PenGjeldendeVedtakDto(
    val fraOgMed: LocalDate
)

data class PenInformasjonOmAvdoedDto(
    val pid: String?,
    val doedsdato: LocalDate?,
    val foersteVirkningsdato: LocalDate? = null,
    val aarligPensjonsgivendeInntektErMinst1G: Boolean? = null,
    val harTilstrekkeligMedlemskapIFolketrygden: Boolean? = null,
    val antallAarUtenlands: Int? = null,
    val erFlyktning: Boolean? = null
)
