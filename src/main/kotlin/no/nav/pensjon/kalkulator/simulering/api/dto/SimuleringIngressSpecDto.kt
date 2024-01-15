package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class SimuleringIngressSpecDto(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: SimuleringGradertUttakIngressDto? = null, // default is helt uttak (100 %)
    val heltUttak: SimuleringHeltUttakIngressDto
)

data class SimuleringGradertUttakIngressDto(
    val grad: Int,
    val uttaksalder: AlderIngressDto,
    val aarligInntektVsaPensjon: Int?
)

data class SimuleringHeltUttakIngressDto(
    val uttaksalder: AlderIngressDto,
    val aarligInntektVsaPensjon: Int,
    val inntektTomAlder: AlderIngressDto? = null
) {
    init {
        require(if (aarligInntektVsaPensjon != 0) inntektTomAlder != null else true) {
            "inntektTomAlder is mandatory for non-zero aarligInntektVsaPensjon"
        }
    }
}

data class AlderIngressDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
