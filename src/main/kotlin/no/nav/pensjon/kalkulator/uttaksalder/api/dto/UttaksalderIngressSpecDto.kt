package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for finding 'første mulige uttaksalder'.
 */
data class UttaksalderIngressSpecDto(
    val sivilstand: Sivilstand?,
    val harEps: Boolean?,
    val sisteInntekt: Int?,
    val simuleringstype: SimuleringType?,
    val gradertUttak: UttaksalderGradertUttakIngressDto? = null, // default is 'helt uttak' (100 %)
)

data class UttaksalderIngressSpecDtoV2(
    val simuleringstype: SimuleringType?,
    val sivilstand: Sivilstand?,
    val harEps: Boolean?,
    val aarligInntekt: Int?, // før første uttak
    val gradertUttak: UttaksalderGradertUttakIngressDtoV2? = null,
    val heltUttak: UttaksalderHeltUttakIngressDtoV2
)

data class UttaksalderGradertUttakIngressDto(
    val grad: Int,
    val aarligInntektVsaPensjon: Int?,
    val heltUttakAlder: UttaksalderAlderDto, // affects gradert uttaksalder
    val foedselsdato: LocalDate
)

data class UttaksalderGradertUttakIngressDtoV2(
    val grad: Int,
    val aarligInntekt: Int?
)

data class UttaksalderHeltUttakIngressDtoV2(
    val uttaksalder: UttaksalderAlderDto,
    val aarligInntektVsaPensjon: UttaksalderInntektDtoV2
)

data class UttaksalderInntektDtoV2(
    val beloep: Int,
    val sluttalder: UttaksalderAlderDto? = null
) {
    init {
        require(if (beloep != 0) sluttalder != null else true) {
            "sluttalder is mandatory for non-zero beloep"
        }
    }
}

data class UttaksalderAlderDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
