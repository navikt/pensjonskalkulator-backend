package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand

data class UttaksalderSpec(
    val pid: Pid,
    val sivilstand: Sivilstand,
    val harEps: Boolean,
    val sisteInntekt: Int,
)
