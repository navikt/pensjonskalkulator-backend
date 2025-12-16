package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

/**
 * Data transfer object for specification of 'simulering offentlig tjenestepensjon' version 2.
 * Changes must be coordinated with consumers of the API.
 */
data class SimuleringOffentligTjenestepensjonSpecV2 (
    @field:NotNull val foedselsdato: LocalDate,
    @field:NotNull val aarligInntektFoerUttakBeloep: Int,
    val gradertUttak: SimuleringOffentligTjenestepensjonGradertUttakV2?,
    @field:NotNull val heltUttak: SimuleringOffentligTjenestepensjonHeltUttakV2,
    @field:NotNull val utenlandsperiodeListe: List<UtenlandsoppholdV2> = emptyList(),
    @field:NotNull val epsHarPensjon: Boolean,
    @field:NotNull val epsHarInntektOver2G: Boolean,
    @field:NotNull val brukerBaOmAfp: Boolean,
    val erApoteker: Boolean?
)

data class UtenlandsoppholdV2 (
    @field:NotNull val fom: LocalDate,
    val tom: LocalDate?
)

data class SimuleringOffentligTjenestepensjonGradertUttakV2(
    @field:NotNull val uttaksalder: SimuleringOffentligTjenestepensjonAlderV2,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class SimuleringOffentligTjenestepensjonHeltUttakV2(
    @field:NotNull val uttaksalder: SimuleringOffentligTjenestepensjonAlderV2,
    val aarligInntektVsaPensjon: SimuleringOffentligTjenestepensjonInntektV2?
)

data class SimuleringOffentligTjenestepensjonInntektV2(
    @field:NotNull val beloep: Int,
    @field:NotNull val sluttAlder: SimuleringOffentligTjenestepensjonAlderV2
)

data class SimuleringOffentligTjenestepensjonAlderV2(@field:NotNull val aar: Int, @field:NotNull val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
