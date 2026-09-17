package no.nav.pensjon.kalkulator.tech.ssl

fun interface SecretPayloadReader {
    fun read(resourceName: String): ByteArray
}
