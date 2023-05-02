package no.nav.pensjon.kalkulator.simulering.client.pen.dto

import java.util.*

data class SimuleringRequestDto(
    val pid: String,
    val sivilstand: PenSivilstand,
    val harEps: Boolean,
    val uttaksar: Int,
    val sisteInntekt: Int,
    val forsteUttaksdato: Date
)
