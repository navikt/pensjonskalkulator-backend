package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

// V1
data class SimuleringSpecDto(
    val simuleringstype: SimuleringType,
    val uttaksgrad: Int,
    val foersteUttaksalder: IngressSimuleringAlderV2,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int?,
    val sivilstand: Sivilstand?
)
