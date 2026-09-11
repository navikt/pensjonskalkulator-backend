package no.nav.pensjon.kalkulator.tech.ssl

import okhttp3.tls.HeldCertificate
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit

internal object TestCertificates {
    val certificateAuthority: HeldCertificate = HeldCertificate.Builder()
        .commonName("Test CA")
        .certificateAuthority(1)
        .build()

    fun clientCertificate(): HeldCertificate = HeldCertificate.Builder()
        .commonName("Norsk Pensjon test client")
        .signedBy(certificateAuthority)
        .build()

    fun expiredClientCertificate(): HeldCertificate {
        val now = System.currentTimeMillis()
        return HeldCertificate.Builder()
            .commonName("Expired Norsk Pensjon test client")
            .validityInterval(now - TimeUnit.DAYS.toMillis(2), now - TimeUnit.DAYS.toMillis(1))
            .signedBy(certificateAuthority)
            .build()
    }

    fun pkcs12(
        client: HeldCertificate = clientCertificate(),
        password: CharArray = charArrayOf(),
        alias: String = "client",
        chain: Array<X509Certificate> = arrayOf(client.certificate, certificateAuthority.certificate)
    ): ByteArray {
        val keyStore = KeyStore.getInstance("PKCS12").apply {
            load(null, password)
            setKeyEntry(alias, client.keyPair.private, password, chain)
        }
        return ByteArrayOutputStream().use {
            keyStore.store(it, password)
            it.toByteArray()
        }
    }

    fun certificateOnlyPkcs12(): ByteArray {
        val keyStore = KeyStore.getInstance("PKCS12").apply {
            load(null, charArrayOf())
            setCertificateEntry("client", clientCertificate().certificate)
        }
        return ByteArrayOutputStream().use {
            keyStore.store(it, charArrayOf())
            it.toByteArray()
        }
    }

    fun trustStore(vararg certificates: X509Certificate): KeyStore =
        KeyStore.getInstance("PKCS12").apply {
            load(null, charArrayOf())
            certificates.forEachIndexed { index, certificate ->
                setCertificateEntry("trusted-$index", certificate)
            }
        }
}
