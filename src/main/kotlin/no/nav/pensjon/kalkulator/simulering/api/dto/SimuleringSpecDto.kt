package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

data class SimuleringSpecDto(
    val simuleringstype: SimuleringType,
    val uttaksgrad: Int,
    val foersteUttaksalder: SimuleringAlderDto,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int?,
    val sivilstand: Sivilstand?
)

data class SimuleringSpecV0Dto(
    val simuleringstype: SimuleringType,
    val forventetInntekt: Int?,
    val uttaksgrad: Int,
    val foersteUttaksalder: SimuleringAlderV0Dto,
    val foedselsdato: LocalDate,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean
)
