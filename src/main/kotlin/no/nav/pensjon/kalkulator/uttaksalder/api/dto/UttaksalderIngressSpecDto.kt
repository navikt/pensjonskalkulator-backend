package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

data class UttaksalderIngressSpecDto(
    val sivilstand: Sivilstand?,
    val harEps: Boolean?,
    val sisteInntekt: Int?,
    val simuleringstype: SimuleringType?,
    val gradertUttak: UttaksalderGradertUttakIngressDto? = null // default is 'helt uttak' (100 %)
)

data class UttaksalderGradertUttakIngressDto(
    val uttaksgrad: Int,
    val inntektUnderGradertUttak: Int?,
    val heltUttakAlder: AlderIngressDto,
    val foedselsdato: LocalDate
)

data class AlderIngressDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
