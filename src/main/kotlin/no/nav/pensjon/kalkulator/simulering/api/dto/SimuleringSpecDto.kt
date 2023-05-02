package no.nav.pensjon.kalkulator.simulering.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

data class SimuleringSpecDto(
    val simuleringstype: String,
    val forventetInntekt: Int,
    val uttaksgrad: Int,
    val foersteUttaksdato: LocalDate,
    val sivilstand: Sivilstand,
    val epsHarInntektOver2G: Boolean
)
