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
    val gradertUttak: UttaksalderGradertUttakIngressDto? = null // default is 'helt uttak' (100 %)
)

data class UttaksalderGradertUttakIngressDto(
    val grad: Int,
    val aarligInntektVsaPensjon: Int?,
    val heltUttakAlder: AlderIngressDto, // affects gradert uttaksalder
    val foedselsdato: LocalDate
)

data class AlderIngressDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
