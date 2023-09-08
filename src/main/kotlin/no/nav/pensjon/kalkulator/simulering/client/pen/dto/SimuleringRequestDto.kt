package no.nav.pensjon.kalkulator.simulering.client.pen.dto

import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringstype
import java.util.*

data class SimuleringRequestDto(
    val pid: String,
    val sivilstand: String,
    val harEps: Boolean,
    val uttaksar: Int,
    val sisteInntekt: Int,
    val forsteUttaksdato: Date,
    val simuleringstype: PenSimuleringstype,
)
