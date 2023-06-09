package no.nav.pensjon.kalkulator.avtale.client.np

data class UttaksperiodeSpec(
    val startAlder: Int,
    val startMaaned: Int,
    val grad: Int,
    val aarligInntekt: Int
)
