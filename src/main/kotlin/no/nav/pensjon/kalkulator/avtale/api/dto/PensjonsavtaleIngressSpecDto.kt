package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.Sivilstand

// V1
data class PensjonsavtaleIngressSpecDto(
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeIngressSpecDto>,
    val antallInntektsaarEtterUttak: Int, // under helt uttak
    val harAfp: Boolean? = false,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int? = 0,
    val sivilstand: Sivilstand? = null
)

// V1
data class UttaksperiodeIngressSpecDto(
    val startAlder: Alder, // månedsverdi 0..11 (antall helt fylte måneder)
    val grad: Int,
    val aarligInntekt: Int
)

data class PensjonsavtaleIngressSpecDtoV2(
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeIngressSpecDtoV2>,
    val harAfp: Boolean? = false,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int? = 0,
    val sivilstand: Sivilstand? = null
)

data class UttaksperiodeIngressSpecDtoV2(
    val startAlder: Alder, // månedsverdi 0..11 (antall helt fylte måneder)
    val grad: Int,
    val aarligInntektVsaPensjon: AvtaleInntektDtoV2
)

data class AvtaleInntektDtoV2(
    val beloep: Int,
    val sluttalder: AvtaleAlderDtoV2? = null
)

data class AvtaleAlderDtoV2(val aar: Int, val maaneder: Int)
