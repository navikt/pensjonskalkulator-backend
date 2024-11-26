package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class IngressSimuleringSpecV7(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val aarligInntektFoerUttakBeloep: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: IngressSimuleringGradertUttakV7? = null, // default is helt uttak (100 %)
    val heltUttak: IngressSimuleringHeltUttakV7,
    val utenlandsperiodeListe: List<UtenlandsperiodeSpecV7>? = null
)

data class IngressSimuleringGradertUttakV7(
    val grad: Int,
    val uttaksalder: IngressSimuleringAlderV7,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class IngressSimuleringHeltUttakV7(
    val uttaksalder: IngressSimuleringAlderV7,
    val aarligInntektVsaPensjon: IngressSimuleringInntektV7?
)

data class IngressSimuleringInntektV7(
    val beloep: Int,
    val sluttAlder: IngressSimuleringAlderV7
)

data class UtenlandsperiodeSpecV7 (
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean
)

data class IngressSimuleringAlderV7(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
