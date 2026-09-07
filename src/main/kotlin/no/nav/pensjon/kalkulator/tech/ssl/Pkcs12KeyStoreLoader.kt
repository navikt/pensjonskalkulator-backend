package no.nav.pensjon.kalkulator.tech.ssl

import java.io.ByteArrayInputStream
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import java.security.cert.X509Certificate

class Pkcs12KeyStoreLoader {

    fun load(payload: ByteArray, password: CharArray, alias: String): KeyStore {
        val keyStore = loadKeyStore(payload, password)
        if (!keyStore.containsAlias(alias)) {
            throw CertificateMaterialException(
                "Norsk Pensjon PKCS#12 payload does not contain the configured alias"
            )
        }
        if (!keyStore.isKeyEntry(alias)) {
            throw CertificateMaterialException(
                "Configured Norsk Pensjon PKCS#12 alias does not refer to a private key"
            )
        }

        val privateKey = keyStore.getKey(alias, password) as? PrivateKey
            ?: throw CertificateMaterialException(
                "Norsk Pensjon PKCS#12 payload does not contain a readable private key"
            )
        val chain = keyStore.getCertificateChain(alias)
            ?.map {
                it as? X509Certificate
                    ?: throw CertificateMaterialException(
                        "Norsk Pensjon PKCS#12 payload contains a non-X.509 certificate"
                    )
            }
            .orEmpty()

        if (chain.isEmpty()) {
            throw CertificateMaterialException("Norsk Pensjon PKCS#12 payload does not contain a client certificate")
        }

        validateValidity(chain)
        validateChain(chain)
        validateKeyPair(privateKey, chain.first())
        return keyStore
    }

    private fun loadKeyStore(payload: ByteArray, password: CharArray): KeyStore =
        try {
            KeyStore.getInstance("PKCS12").apply {
                ByteArrayInputStream(payload).use { load(it, password) }
            }
        } catch (exception: IOException) {
            throw invalidPkcs12(exception)
        } catch (exception: GeneralSecurityException) {
            throw invalidPkcs12(exception)
        }

    private fun invalidPkcs12(cause: Exception) =
        CertificateMaterialException(
            "Norsk Pensjon certificate secret is not a valid PKCS#12 payload or the password is incorrect",
            cause
        )

    private fun validateValidity(chain: List<X509Certificate>) {
        chain.forEach {
            try {
                it.checkValidity()
            } catch (exception: CertificateExpiredException) {
                throw CertificateMaterialException("Norsk Pensjon certificate chain contains an expired certificate")
            } catch (exception: CertificateNotYetValidException) {
                throw CertificateMaterialException("Norsk Pensjon certificate chain contains a certificate not yet valid")
            }
        }
    }

    private fun validateChain(chain: List<X509Certificate>) {
        chain.zipWithNext().forEach { (certificate, issuer) ->
            if (certificate.issuerX500Principal != issuer.subjectX500Principal) {
                throw CertificateMaterialException("Norsk Pensjon PKCS#12 certificate chain is incomplete or unordered")
            }
            try {
                certificate.verify(issuer.publicKey)
            } catch (exception: GeneralSecurityException) {
                throw CertificateMaterialException(
                    "Norsk Pensjon PKCS#12 certificate chain has an invalid signature",
                    exception
                )
            }
        }
    }

    private fun validateKeyPair(privateKey: PrivateKey, certificate: X509Certificate) {
        val algorithm = when (privateKey.algorithm.uppercase()) {
            "RSA" -> "SHA256withRSA"
            "EC", "ECDSA" -> "SHA256withECDSA"
            "DSA" -> "SHA256withDSA"
            else -> throw CertificateMaterialException(
                "Norsk Pensjon PKCS#12 contains an unsupported private-key algorithm"
            )
        }
        try {
            val content = "norsk-pensjon-key-validation".toByteArray()
            val signature = Signature.getInstance(algorithm).apply {
                initSign(privateKey)
                update(content)
            }.sign()
            val valid = Signature.getInstance(algorithm).run {
                initVerify(certificate.publicKey)
                update(content)
                verify(signature)
            }
            if (!valid) {
                throw CertificateMaterialException(
                    "Norsk Pensjon private key does not match the client certificate"
                )
            }
        } catch (exception: CertificateMaterialException) {
            throw exception
        } catch (exception: Exception) {
            throw CertificateMaterialException(
                "Unable to verify the Norsk Pensjon private key and client certificate",
                exception
            )
        }
    }
}

class CertificateMaterialException(message: String, cause: Throwable? = null) :
    IllegalStateException(message, cause)
