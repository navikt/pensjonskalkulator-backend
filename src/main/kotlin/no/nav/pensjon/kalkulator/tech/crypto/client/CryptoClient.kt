package no.nav.pensjon.kalkulator.tech.crypto.client

interface CryptoClient {

    fun encrypt(value: String): String
    fun decrypt(value: String): String
}