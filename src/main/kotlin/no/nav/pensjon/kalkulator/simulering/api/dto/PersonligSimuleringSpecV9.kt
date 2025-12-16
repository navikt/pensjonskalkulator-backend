package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Versjon 9 av DTO (data transfer object) som representerer spesifikasjon ("spec")
 * for simulering av alderspensjon i personlig (innlogget) kontekst.
 */
data class PersonligSimuleringSpecV9(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val aarligInntektFoerUttakBeloep: Int?,
    val gradertUttak: PersonligSimuleringGradertUttakSpecV9? = null, // default is helt uttak (100 %)
    val heltUttak: PersonligSimuleringHeltUttakSpecV9,
    val utenlandsperiodeListe: List<PersonligSimuleringUtenlandsperiodeSpecV9>? = null,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean? = null,
    val epsHarPensjon: Boolean? = null,
    val afpInntektMaanedFoerUttak: Boolean?,
    val afpOrdning: AfpOrdningType? = null,
    val innvilgetLivsvarigOffentligAfp: List<PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV9>? = null
)

data class PersonligSimuleringGradertUttakSpecV9(
    val grad: Int,
    val uttaksalder: PersonligSimuleringAlderSpecV9,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class PersonligSimuleringHeltUttakSpecV9(
    val uttaksalder: PersonligSimuleringAlderSpecV9,
    val aarligInntektVsaPensjon: PersonligSimuleringInntektSpecV9?
)

data class PersonligSimuleringInntektSpecV9(
    val beloep: Int,
    val sluttAlder: PersonligSimuleringAlderSpecV9
)

data class PersonligSimuleringUtenlandsperiodeSpecV9(
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean
)

/**
 * Spesifiserer egenskapene til en løpende livsvarig AFP i offentlig sektor.
 */
data class PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV9(
    val aarligBruttoBeloep: Double,
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val uttakFom: LocalDate,
    val sistRegulertGrunnbeloep: Int? = null
)

data class PersonligSimuleringAlderSpecV9(
    val aar: Int,
    val maaneder: Int
) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
