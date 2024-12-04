package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand

data class PensjonsavtaleSpecV3(
    val aarligInntektFoerUttakBeloep: Int,
    val uttaksperioder: List<PensjonsavtaleUttaksperiodeSpecV3>,
    val harAfp: Boolean? = false,
    val sivilstand: PensjonsavtaleSivilstandSpecV3? = null,
    val epsHarInntektOver2G: Boolean,
    val epsHarPensjon: Boolean
)

data class PensjonsavtaleUttaksperiodeSpecV3(
    val startAlder: PensjonsavtaleAlderSpecV3,
    val grad: Int,
    val aarligInntektVsaPensjon: PensjonsavtaleInntektSpecV3?
)

data class PensjonsavtaleInntektSpecV3(
    val beloep: Int,
    val sluttAlder: PensjonsavtaleAlderSpecV3? = null
)

data class PensjonsavtaleAlderSpecV3(
    val aar: Int,
    val maaneder: Int
)

enum class PensjonsavtaleSivilstandSpecV3(val internalValue: Sivilstand) {
    UNKNOWN(internalValue = Sivilstand.UNKNOWN),
    UOPPGITT(internalValue = Sivilstand.UOPPGITT),
    UGIFT(internalValue = Sivilstand.UGIFT),
    GIFT(internalValue = Sivilstand.GIFT),
    ENKE_ELLER_ENKEMANN(internalValue = Sivilstand.ENKE_ELLER_ENKEMANN),
    SKILT(internalValue = Sivilstand.SKILT),
    SEPARERT(internalValue = Sivilstand.SEPARERT),
    REGISTRERT_PARTNER(internalValue = Sivilstand.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(internalValue = Sivilstand.SEPARERT_PARTNER),
    SKILT_PARTNER(internalValue = Sivilstand.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(internalValue = Sivilstand.GJENLEVENDE_PARTNER),
    SAMBOER(internalValue = Sivilstand.SAMBOER);
}
