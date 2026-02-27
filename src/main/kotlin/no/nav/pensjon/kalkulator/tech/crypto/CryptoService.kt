package no.nav.pensjon.kalkulator.tech.crypto

import no.nav.pensjon.kalkulator.tech.crypto.client.CryptoClient
import org.springframework.stereotype.Service

@Service
class CryptoService(private val cryptoClient: CryptoClient) {

    fun encrypt(value: String?): String =
        value?.let(cryptoClient::encrypt) ?: ""

    fun decrypt(value: String?): String =
        value?.let(cryptoClient::decrypt) ?: ""
}
