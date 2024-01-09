package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

data class SimuleringIngressSpecDto(
    val simuleringstype: SimuleringType,
    val foersteUttaksalder: AlderIngressDto,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: SimuleringGradertUttakIngressDto? = null // default is helt uttak (100 %)
)

class SimuleringGradertUttakIngressDto(
    val uttaksgrad: Int,
    val inntektUnderGradertUttak: Int?,
    val heltUttakAlder: AlderIngressDto
    // foedselsdato is in parent class
)

data class AlderIngressDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
