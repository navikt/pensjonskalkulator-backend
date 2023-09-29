package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType

data class UttaksalderIngressSpecDto(
    val sivilstand: Sivilstand?,
    val harEps: Boolean?,
    val sisteInntekt: Int?,
    val simuleringstype: SimuleringType?,
) {
    companion object {
        fun empty() = UttaksalderIngressSpecDto(null, null, null, null)
    }
}
