package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Versjon 8 av DTO (data transfer object) som representerer spesifikasjon ("spec")
 * for simulering av alderspensjon i personlig (innlogget) kontekst.
 */
data class PersonligSimuleringSpecV8(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val aarligInntektFoerUttakBeloep: Int?,
    val gradertUttak: PersonligSimuleringGradertUttakSpecV8? = null, // default is helt uttak (100 %)
    val heltUttak: PersonligSimuleringHeltUttakSpecV8,
    val utenlandsperiodeListe: List<PersonligSimuleringUtenlandsperiodeSpecV8>? = null,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean,
    val epsHarPensjon: Boolean,
    val afpInntektMaanedFoerUttak: Int?,
    val afpOrdning: AfpOrdningType? = null
)

data class PersonligSimuleringGradertUttakSpecV8(
    val grad: Int,
    val uttaksalder: PersonligSimuleringAlderSpecV8,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class PersonligSimuleringHeltUttakSpecV8(
    val uttaksalder: PersonligSimuleringAlderSpecV8,
    val aarligInntektVsaPensjon: PersonligSimuleringInntektSpecV8?
)

data class PersonligSimuleringInntektSpecV8(
    val beloep: Int,
    val sluttAlder: PersonligSimuleringAlderSpecV8
)

data class PersonligSimuleringUtenlandsperiodeSpecV8(
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean
)

data class PersonligSimuleringAlderSpecV8(
    val aar: Int,
    val maaneder: Int
){
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
