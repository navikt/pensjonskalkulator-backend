package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

data class SimuleringSpec(
    val simuleringstype: String,
    val pid: Pid,
    val forventetInntekt: Int,
    val uttaksgrad: Int,
    val foersteUttaksdato: LocalDate,
    val sivilstand: Sivilstand,
    val epsHarInntektOver2G: Boolean
)
