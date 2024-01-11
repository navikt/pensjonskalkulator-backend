package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

data class SimuleringIngressSpecDto(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: SimuleringGradertUttakIngressDto? = null, // default is helt uttak (100 %)
    val heltUttak: SimuleringHeltUttakIngressDto
)

class SimuleringGradertUttakIngressDto(
    val grad: Int,
    val uttakFomAlder: AlderIngressDto,
    val aarligInntektVsaPensjon: Int?
)

class SimuleringHeltUttakIngressDto(
    val uttakFomAlder: AlderIngressDto,
    val aarligInntektVsaPensjon: Int,
    val inntektTomAlder: AlderIngressDto
)

data class AlderIngressDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
