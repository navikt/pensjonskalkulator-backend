package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

data class UttaksalderSpecV2(
    val simuleringstype: SimuleringType?,
    val aarligInntektFoerUttakBeloep: Int?,
    val aarligInntektVsaPensjon: UttaksalderInntektSpecV2?,
    val utenlandsperiodeListe: List<UttaksalderUtenlandsperiodeSpecV2>? = null,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean,
    val epsHarPensjon: Boolean
)

data class UttaksalderInntektSpecV2(
    val beloep: Int,
    val sluttAlder: UttaksalderAlderSpecV2? = null
) {
    init {
        require(if (beloep != 0) sluttAlder != null else true) {
            "sluttAlder is mandatory for non-zero beloep"
        }
    }
}

data class UttaksalderAlderSpecV2(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}

data class UttaksalderUtenlandsperiodeSpecV2(
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean
)
