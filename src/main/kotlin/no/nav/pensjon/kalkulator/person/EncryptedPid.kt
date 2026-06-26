package no.nav.pensjon.kalkulator.person

/**
 * Represents an encrypted person identifier.
 */
data class EncryptedPid(val value: String) {
    constructor(pid: PossiblyEncryptedPid) : this(validated(pid).value)

    private companion object {
        private fun validated(value: PossiblyEncryptedPid): PossiblyEncryptedPid {
            require(value.isEncrypted) { "Expected encrypted PID" }
            return value
        }
    }
}