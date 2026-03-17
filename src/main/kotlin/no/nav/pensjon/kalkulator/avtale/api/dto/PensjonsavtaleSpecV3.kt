package no.nav.pensjon.kalkulator.avtale.api.dto

import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Sivilstatus

data class PensjonsavtaleSpecV3(
    @field:NotNull val aarligInntektFoerUttakBeloep: Int,
    @field:NotNull val uttaksperioder: List<PensjonsavtaleUttaksperiodeSpecV3>,
    val harAfp: Boolean? = false,
    val sivilstand: PensjonsavtaleSivilstandSpecV3? = null,
    @field:NotNull val epsHarInntektOver2G: Boolean,
    @field:NotNull val epsHarPensjon: Boolean
)

data class PensjonsavtaleUttaksperiodeSpecV3(
    @field:NotNull val startAlder: PensjonsavtaleAlderSpecV3,
    @field:NotNull val grad: Int,
    val aarligInntektVsaPensjon: PensjonsavtaleInntektSpecV3?
)

data class PensjonsavtaleInntektSpecV3(
    @field:NotNull val beloep: Int,
    val sluttAlder: PensjonsavtaleAlderSpecV3? = null
)

data class PensjonsavtaleAlderSpecV3(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)

enum class PensjonsavtaleSivilstandSpecV3(val internalValue: Sivilstatus) {
    UNKNOWN(internalValue = Sivilstatus.UNKNOWN),
    UOPPGITT(internalValue = Sivilstatus.UOPPGITT),
    UGIFT(internalValue = Sivilstatus.UGIFT),
    GIFT(internalValue = Sivilstatus.GIFT),
    ENKE_ELLER_ENKEMANN(internalValue = Sivilstatus.ENKE_ELLER_ENKEMANN),
    SKILT(internalValue = Sivilstatus.SKILT),
    SEPARERT(internalValue = Sivilstatus.SEPARERT),
    REGISTRERT_PARTNER(internalValue = Sivilstatus.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(internalValue = Sivilstatus.SEPARERT_PARTNER),
    SKILT_PARTNER(internalValue = Sivilstatus.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(internalValue = Sivilstatus.GJENLEVENDE_PARTNER),
    SAMBOER(internalValue = Sivilstatus.SAMBOER)
}
