package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class IngressSimuleringSpecV2(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val aarligInntektFoerUttakBeloep: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: IngressSimuleringGradertUttakV2? = null, // default is helt uttak (100 %)
    val heltUttak: IngressSimuleringHeltUttakV2
)

data class IngressSimuleringGradertUttakV2(
    val grad: Int,
    val uttaksalder: IngressSimuleringAlderV2,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class IngressSimuleringHeltUttakV2(
    val uttaksalder: IngressSimuleringAlderV2,
    val aarligInntektVsaPensjon: IngressSimuleringInntektV2?
)

data class IngressSimuleringInntektV2(
    val beloep: Int,
    val sluttAlder: IngressSimuleringAlderV2
)

data class IngressSimuleringAlderV2(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
