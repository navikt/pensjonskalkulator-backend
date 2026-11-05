package no.nav.pensjon.kalkulator.opptjening.behandlet

/**
 * Representerer en merknad relatert til pensjonsopptjening.
 */
data class Merknad(
    val kode: MerknadCode,
    val uttaksgrad: UttaksgradMerknad? = null
)