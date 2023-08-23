package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.Uttaksalder
import java.time.LocalDate

data class SimuleringSpecDto(
    val simuleringstype: SimuleringType,
    val forventetInntekt: Int?,
    val uttaksgrad: Int,
    val foersteUttaksalder: Uttaksalder,
    val foedselsdato: LocalDate,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean
)
