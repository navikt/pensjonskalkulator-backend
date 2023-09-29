package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType

data class UttaksalderSpec(
    val pid: Pid,
    val sivilstand: Sivilstand,
    val harEps: Boolean,
    val sisteInntekt: Int,
    val simuleringType: SimuleringType,
)
