package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class IngressSimuleringSpecV3(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val aarligInntektFoerUttakBeloep: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: IngressSimuleringGradertUttakV3? = null, // default is helt uttak (100 %)
    val heltUttak: IngressSimuleringHeltUttakV3
)

data class IngressSimuleringGradertUttakV3(
    val grad: Int,
    val uttaksalder: IngressSimuleringAlderV3,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class IngressSimuleringHeltUttakV3(
    val uttaksalder: IngressSimuleringAlderV3,
    val aarligInntektVsaPensjon: IngressSimuleringInntektV3?
)

data class IngressSimuleringInntektV3(
    val beloep: Int,
    val sluttAlder: IngressSimuleringAlderV3
)

data class IngressSimuleringAlderV3(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
