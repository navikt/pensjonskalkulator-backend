package no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto

data class UttaksalderEgressSpecDto(
    val pid: String,
    val sivilstand: String,
    val harEps: Boolean,
    val sisteInntekt: Int,
    val simuleringType: String,
)
