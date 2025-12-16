package no.nav.pensjon.kalkulator.simulering.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Versjon 9 av DTO (data transfer object) som representerer spesifikasjon ("spec")
 * for simulering av alderspensjon i personlig (innlogget) kontekst.
 */
data class PersonligSimuleringSpecV9(
    @field:NotNull val simuleringstype: SimuleringType,
    @field:NotNull val foedselsdato: LocalDate,
    val aarligInntektFoerUttakBeloep: Int?,
    val gradertUttak: PersonligSimuleringGradertUttakSpecV9? = null, // default is helt uttak (100 %)
    @field:NotNull val heltUttak: PersonligSimuleringHeltUttakSpecV9,
    val utenlandsperiodeListe: List<PersonligSimuleringUtenlandsperiodeSpecV9>? = null,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean? = null,
    val epsHarPensjon: Boolean? = null,
    val afpInntektMaanedFoerUttak: Boolean?,
    val afpOrdning: AfpOrdningType? = null,
    val innvilgetLivsvarigOffentligAfp: List<PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV9>? = null
)

data class PersonligSimuleringGradertUttakSpecV9(
    @field:NotNull val grad: Int,
    @field:NotNull val uttaksalder: PersonligSimuleringAlderSpecV9,
    val aarligInntektVsaPensjonBeloep: Int?
)

data class PersonligSimuleringHeltUttakSpecV9(
    @field:NotNull val uttaksalder: PersonligSimuleringAlderSpecV9,
    val aarligInntektVsaPensjon: PersonligSimuleringInntektSpecV9?
)

data class PersonligSimuleringInntektSpecV9(
    @field:NotNull val beloep: Int,
    @field:NotNull val sluttAlder: PersonligSimuleringAlderSpecV9
)

data class PersonligSimuleringUtenlandsperiodeSpecV9(
    @field:NotNull  val fom: LocalDate,
    val tom: LocalDate?,
    @field:NotNull val landkode: String,
    @field:NotNull val arbeidetUtenlands: Boolean
)

/**
 * Spesifiserer egenskapene til en løpende livsvarig AFP i offentlig sektor.
 */
data class PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV9(
    @field:NotNull val aarligBruttoBeloep: Double,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val uttakFom: LocalDate,
    val sistRegulertGrunnbeloep: Int? = null
)

data class PersonligSimuleringAlderSpecV9(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
