package no.nav.pensjon.kalkulator.merknad

data class Merknad(
    val kode: MerknadCode,
    val uttaksgrad: UttaksgradMerknad? = null
)