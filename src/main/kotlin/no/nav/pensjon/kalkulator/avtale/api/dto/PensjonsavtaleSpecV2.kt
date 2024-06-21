package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand

data class PensjonsavtaleSpecV2(
    val aarligInntektFoerUttakBeloep: Int,
    val uttaksperioder: List<PensjonsavtaleUttaksperiodeSpecV2>,
    val harAfp: Boolean? = false,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int? = 0,
    val sivilstand: Sivilstand? = null
)

data class PensjonsavtaleUttaksperiodeSpecV2(
    val startAlder: PensjonsavtaleAlderSpecV2,
    val grad: Int,
    val aarligInntektVsaPensjon: PensjonsavtaleInntektSpecV2?
)

data class PensjonsavtaleInntektSpecV2(
    val beloep: Int,
    val sluttAlder: PensjonsavtaleAlderSpecV2? = null
)

data class PensjonsavtaleAlderSpecV2(val aar: Int, val maaneder: Int)
