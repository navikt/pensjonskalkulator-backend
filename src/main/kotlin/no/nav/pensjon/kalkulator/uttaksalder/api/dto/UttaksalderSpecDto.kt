package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand

data class UttaksalderSpecDto(
    val sivilstand: Sivilstand?,
    val harEps: Boolean?,
    val sisteInntekt: Int?,
) {
    companion object {
        fun empty() = UttaksalderSpecDto(null, null, null)
    }
}