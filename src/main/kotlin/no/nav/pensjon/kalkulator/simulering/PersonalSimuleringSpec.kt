package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand

data class PersonalSimuleringSpec(
    val pid: Pid,
    val forventetInntekt: Int,
    val sivilstand: Sivilstand
)
