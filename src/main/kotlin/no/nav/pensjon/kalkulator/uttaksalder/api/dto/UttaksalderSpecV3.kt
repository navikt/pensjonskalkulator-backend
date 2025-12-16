package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

data class UttaksalderSpecV3(
    val simuleringstype: SimuleringType?,
    val aarligInntektFoerUttakBeloep: Int?,
    val aarligInntektVsaPensjon: UttaksalderInntektSpecV3?,
    val utenlandsperiodeListe: List<UttaksalderUtenlandsperiodeSpecV3>? = null,
    val sivilstand: Sivilstand?,
    @field:NotNull val epsHarInntektOver2G: Boolean,
    @field:NotNull val epsHarPensjon: Boolean,
    val innvilgetLivsvarigOffentligAfp: List<PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV3>? = null
)

data class UttaksalderInntektSpecV3(
    @field:NotNull val beloep: Int,
    val sluttAlder: UttaksalderAlderSpecV3? = null
) {
    init {
        require(if (beloep != 0) sluttAlder != null else true) {
            "sluttAlder is mandatory for non-zero beloep"
        }
    }
}

data class UttaksalderAlderSpecV3(@field:NotNull val aar: Int, @field:NotNull val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}

data class UttaksalderUtenlandsperiodeSpecV3(
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean
)

/**
 * Spesifiserer egenskapene til en løpende livsvarig AFP i offentlig sektor.
 */
data class PersonligSimuleringInnvilgetLivsvarigOffentligAfpSpecV3(
    val aarligBruttoBeloep: Double,
    @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val uttakFom: LocalDate,
    val sistRegulertGrunnbeloep: Int? = null
)
