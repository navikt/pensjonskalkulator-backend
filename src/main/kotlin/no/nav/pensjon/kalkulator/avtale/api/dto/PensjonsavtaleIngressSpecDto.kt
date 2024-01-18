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

data class IngressPensjonsavtaleSpecV2(
    val aarligInntektFoerUttakBeloep: Int,
    val uttaksperioder: List<IngressPensjonsavtaleUttaksperiodeV2>,
    val harAfp: Boolean? = false,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int? = 0,
    val sivilstand: Sivilstand? = null
)

data class IngressPensjonsavtaleUttaksperiodeV2(
    val startAlder: Alder, // månedsverdi 0..11 (antall helt fylte måneder)
    val grad: Int,
    val aarligInntektVsaPensjon: IngressPensjonsavtaleInntektV2?
)

data class IngressPensjonsavtaleInntektV2(
    val beloep: Int,
    val sluttAlder: IngressPensjonsavtaleAlderV2? = null
)

data class IngressPensjonsavtaleAlderV2(val aar: Int, val maaneder: Int)
