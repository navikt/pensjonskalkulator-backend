package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class IngressSimuleringSpecV6(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val aarligInntektFoerUttakBeloep: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: IngressSimuleringGradertUttakV6? = null, // default is helt uttak (100 %)
    val heltUttak: IngressSimuleringHeltUttakV6
)

data class IngressSimuleringGradertUttakV6(
    val grad: Int,
    val uttaksalder: IngressSimuleringAlderV6,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class IngressSimuleringHeltUttakV6(
    val uttaksalder: IngressSimuleringAlderV6,
    val aarligInntektVsaPensjon: IngressSimuleringInntektV6?
)

data class IngressSimuleringInntektV6(
    val beloep: Int,
    val sluttAlder: IngressSimuleringAlderV6
)

data class IngressSimuleringAlderV6(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
