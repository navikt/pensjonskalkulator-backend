package no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto

import java.util.*

data class UttaksalderEgressSpecDto(
    val pid: String,
    val sivilstand: String,
    val harEps: Boolean,
    val sisteInntekt: Int,
    val simuleringType: String,
    val uttaksgrad: String?, // default is 100 %
    val inntektUnderGradertUttak: Int?, // required if uttaksgrad < 100 %
    val heltUttakDato: Date? // required if uttaksgrad < 100 %
)
