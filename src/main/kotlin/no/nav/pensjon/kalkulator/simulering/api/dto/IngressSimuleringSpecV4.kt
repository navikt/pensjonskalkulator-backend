package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class IngressSimuleringSpecV4(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val aarligInntektFoerUttakBeloep: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: IngressSimuleringGradertUttakV4? = null, // default is helt uttak (100 %)
    val heltUttak: IngressSimuleringHeltUttakV4
)

data class IngressSimuleringGradertUttakV4(
    val grad: Int,
    val uttaksalder: IngressSimuleringAlderV4,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class IngressSimuleringHeltUttakV4(
    val uttaksalder: IngressSimuleringAlderV4,
    val aarligInntektVsaPensjon: IngressSimuleringInntektV4?
)

data class IngressSimuleringInntektV4(
    val beloep: Int,
    val sluttAlder: IngressSimuleringAlderV4
)

data class IngressSimuleringAlderV4(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
