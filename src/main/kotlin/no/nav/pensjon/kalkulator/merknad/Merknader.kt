package no.nav.pensjon.kalkulator.merknad

data class Merknader(
    val perAar: Map<Int, List<MerknadCode>>
)