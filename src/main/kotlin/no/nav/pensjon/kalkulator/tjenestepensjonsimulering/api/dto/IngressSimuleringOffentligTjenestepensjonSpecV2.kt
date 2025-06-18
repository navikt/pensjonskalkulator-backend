package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import java.time.LocalDate

data class IngressSimuleringOffentligTjenestepensjonSpecV2 (
    val foedselsdato: LocalDate,
    val aarligInntektFoerUttakBeloep: Int,
    val gradertUttak: SimuleringOffentligTjenestepensjonGradertUttakV2?,
    val heltUttak: SimuleringOffentligTjenestepensjonHeltUttakV2,
    val utenlandsperiodeListe: List<UtenlandsoppholdV2> = emptyList(),
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val brukerBaOmAfp: Boolean,
    val erApoteker: Boolean?,
)

data class UtenlandsoppholdV2 (
    val fom: LocalDate,
    val tom: LocalDate?
)

data class SimuleringOffentligTjenestepensjonGradertUttakV2(
    val uttaksalder: SimuleringOffentligTjenestepensjonAlderV2,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class SimuleringOffentligTjenestepensjonHeltUttakV2(
    val uttaksalder: SimuleringOffentligTjenestepensjonAlderV2,
    val aarligInntektVsaPensjon: SimuleringOffentligTjenestepensjonInntektV2?
)

data class SimuleringOffentligTjenestepensjonInntektV2(
    val beloep: Int,
    val sluttAlder: SimuleringOffentligTjenestepensjonAlderV2
)

data class SimuleringOffentligTjenestepensjonAlderV2(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}