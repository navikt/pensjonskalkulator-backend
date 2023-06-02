package no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto

import no.nav.pensjon.kalkulator.pen.PenSivilstand

data class UttaksalderRequestDto(
    val pid: String,
    val sivilstand: PenSivilstand,
    val harEps: Boolean,
    val sisteInntekt: Int,
)
