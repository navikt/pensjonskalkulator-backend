package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class SimuleringOffentligTjenestepensjonSpecV3 (
    @field:NotNull val foedselsdato: LocalDate,
    @field:NotNull val aarligInntektFoerUttakBeloep: Int,
    val gradertUttak: SimuleringOffentligTjenestepensjonGradertUttakV3?,
    @field:NotNull val heltUttak: SimuleringOffentligTjenestepensjonHeltUttakV3,
    @field:NotNull val utenlandsperiodeListe: List<UtenlandsoppholdV3> = emptyList(),
    @field:NotNull val epsHarPensjon: Boolean,
    @field:NotNull val epsHarInntektOver2G: Boolean,
    @field:NotNull val brukerBaOmAfp: Boolean,
    val erApoteker: Boolean?
)

data class UtenlandsoppholdV3 (
    @field:NotNull val fom: LocalDate,
    val tom: LocalDate?
)

data class SimuleringOffentligTjenestepensjonGradertUttakV3(
    @field:NotNull val uttaksalder: SimuleringOffentligTjenestepensjonAlderV3,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class SimuleringOffentligTjenestepensjonHeltUttakV3(
    @field:NotNull val uttaksalder: SimuleringOffentligTjenestepensjonAlderV3,
    val aarligInntektVsaPensjon: SimuleringOffentligTjenestepensjonInntektV3?
)

data class SimuleringOffentligTjenestepensjonInntektV3(
    @field:NotNull val beloep: Int,
    @field:NotNull val sluttAlder: SimuleringOffentligTjenestepensjonAlderV3
)

data class SimuleringOffentligTjenestepensjonAlderV3(@field:NotNull val aar: Int,@field:NotNull val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
