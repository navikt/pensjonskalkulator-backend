package no.nav.pensjon.kalkulator.tech.crypto

object EncryptionDetector {

    private const val PID_ENCRYPTION_MARK = "."

    fun isEncryptedPid(value: String): Boolean =
        value.contains(PID_ENCRYPTION_MARK)
}