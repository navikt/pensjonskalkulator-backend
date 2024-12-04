package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

data class IngressUttaksalderSpecForHeltUttakV1(
    val simuleringstype: SimuleringType?,
    val sivilstand: Sivilstand?,
    val harEps: Boolean?,
    val aarligInntektFoerUttakBeloep: Int?,
    val aarligInntektVsaPensjon: IngressUttaksalderInntektV1?,
    val utenlandsperiodeListe: List<UttaksalderUtenlandsperiodeSpecV1>? = null
)

data class IngressUttaksalderInntektV1(
    val beloep: Int,
    val sluttAlder: IngressUttaksalderAlderV1? = null
) {
    init {
        require(if (beloep != 0) sluttAlder != null else true) {
            "sluttAlder is mandatory for non-zero beloep"
        }
    }
}

data class IngressUttaksalderGradertUttakV1(
    val grad: Int,
    val aarligInntektVsaPensjonBeloep: Int?
)

/**
 * For å finne første mulige uttaksalder for gradert uttak, må man bl.a. vite:
 * (1) Sluttalder for gradert uttak (som er det samme som startalder for helt uttak)
 * (2) Inntekt under gradert uttak
 * (3) Inntekt under helt uttak
 * Denne klassen håndterer verdi (1) og (3)
 */
data class IngressUttaksalderHeltUttakV1(
    val uttaksalder: IngressUttaksalderAlderV1, // angir implisitt sluttalder for gradert uttak
    val aarligInntektVsaPensjon: IngressUttaksalderInntektV1?
)

data class IngressUttaksalderAlderV1(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}

data class UttaksalderUtenlandsperiodeSpecV1(
    val fom: LocalDate,
    val tom: LocalDate?,
    val landkode: String,
    val arbeidetUtenlands: Boolean
)
