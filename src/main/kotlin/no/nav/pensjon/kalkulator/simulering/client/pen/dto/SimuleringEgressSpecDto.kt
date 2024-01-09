package no.nav.pensjon.kalkulator.simulering.client.pen.dto

import java.util.*

data class SimuleringEgressSpecDto(
    val pid: String,
    val sivilstand: String,
    val harEps: Boolean,
    val uttaksar: Int,
    val sisteInntekt: Int,
    val forsteUttaksdato: Date,
    val simuleringstype: String,
    val uttaksgrad: String?, // default is 100 %
    val inntektUnderGradertUttak: Int?, // required if uttaksgrad < 100 %
    val heltUttakDato: Date? // required if uttaksgrad < 100 %
)
