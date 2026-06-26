package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.tech.crypto.EncryptionDetector.isEncryptedPid

/**
 * Represents a person identifier that may be encrypted.
 */
data class PossiblyEncryptedPid(val value: String) {
    val isEncrypted: Boolean = isEncryptedPid(value)
}