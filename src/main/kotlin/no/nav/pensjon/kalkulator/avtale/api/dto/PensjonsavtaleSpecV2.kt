package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

data class PensjonsavtaleSpecV2(
    val aarligInntektFoerUttakBeloep: Int,
    val uttaksperioder: List<PensjonsavtaleUttaksperiodeSpecV2>,
    val harAfp: Boolean? = false,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int? = 0,
    val utenlandsperioder: List<PensjonsavtaleOppholdSpecV2>? = null,
    val sivilstand: PensjonsavtaleSivilstandSpecV2? = null
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

data class PensjonsavtaleAlderSpecV2(
    val aar: Int,
    val maaneder: Int
)

data class PensjonsavtaleOppholdSpecV2 (
    val fom: LocalDate,
    val tom: LocalDate?
)

enum class PensjonsavtaleSivilstandSpecV2(val internalValue: Sivilstand) {
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

  //  companion object {
  //      fun toInternalValue(sivilstand: PensjonsavtaleSivilstandSpecV2): Sivilstand =
  //          PensjonsavtaleSivilstandSpecV2.entries.firstOrNull { it.internalValue == sivilstand } ?: UDEFINERT
  //  }
}
