package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * Incoming (ingress) data transfer object (DTO) containing specification for 'simulering av alderspensjon'.
 */
data class SimuleringIngressSpecDtoV2(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int?,
    val sivilstand: Sivilstand?,
    val gradertUttak: SimuleringGradertUttakIngressDtoV2? = null, // default is helt uttak (100 %)
    val heltUttak: SimuleringHeltUttakIngressDtoV2
)

data class SimuleringGradertUttakIngressDtoV2(
    val grad: Int,
    val uttaksalder: SimuleringAlderDto,
    val aarligInntekt: Int?
)

data class SimuleringHeltUttakIngressDtoV2(
    val uttaksalder: SimuleringAlderDto,
    val aarligInntektVsaPensjon: SimuleringInntektDtoV2
)

data class SimuleringInntektDtoV2(
    val beloep: Int,
    val sluttalder: SimuleringAlderDto? = null
) {
    init {
        require(if (beloep != 0) sluttalder != null else true) {
            "sluttalder is mandatory for non-zero beloep"
        }
    }
}

data class SimuleringAlderDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}
