package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.tech.crypto.EncryptionDetector.isEncryptedPid

/**
 * Represents an encrypted person identifier.
 */
data class EncryptedPid(val value: String) {

    constructor(pid: PossiblyEncryptedPid) : this(pid.value)

    init {
        require(isEncryptedPid(value)) { "Expected encrypted PID" }
    }
}