package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class IngressSimuleringSpecV5(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val aarligInntektFoerUttakBeloep: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: IngressSimuleringGradertUttakV5? = null, // default is helt uttak (100 %)
    val heltUttak: IngressSimuleringHeltUttakV5
)

data class IngressSimuleringGradertUttakV5(
    val grad: Int,
    val uttaksalder: IngressSimuleringAlderV5,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class IngressSimuleringHeltUttakV5(
    val uttaksalder: IngressSimuleringAlderV5,
    val aarligInntektVsaPensjon: IngressSimuleringInntektV5?
)

data class IngressSimuleringInntektV5(
    val beloep: Int,
    val sluttAlder: IngressSimuleringAlderV5
)

data class IngressSimuleringAlderV5(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
