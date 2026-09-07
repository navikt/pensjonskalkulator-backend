package no.nav.pensjon.kalkulator.tech.ssl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class Pkcs12KeyStoreLoaderTest : FunSpec({
    val loader = Pkcs12KeyStoreLoader()
    val password = "password".toCharArray()
    val alias = "nav integrasjon norsk pensjon"

    test("loads password-protected PKCS12 using configured alias") {
        val keyStore = loader.load(
            TestCertificates.pkcs12(password = password, alias = alias),
            password,
            alias
        )

        keyStore.isKeyEntry(alias) shouldBe true
        keyStore.getCertificateChain(alias).size shouldBe 2
    }

    test("rejects malformed payload") {
        shouldThrow<CertificateMaterialException> {
            loader.load("not-pkcs12".toByteArray(), password, alias)
        }.message shouldContain "not a valid PKCS#12 payload"
    }

    test("rejects incorrect password") {
        shouldThrow<CertificateMaterialException> {
            loader.load(TestCertificates.pkcs12(password = password, alias = alias), "wrong".toCharArray(), alias)
        }.message shouldContain "password is incorrect"
    }

    test("rejects missing configured alias") {
        shouldThrow<CertificateMaterialException> {
            loader.load(TestCertificates.pkcs12(password = password), password, alias)
        }.message shouldContain "does not contain the configured alias"
    }

    test("rejects payload without private key") {
        shouldThrow<CertificateMaterialException> {
            loader.load(TestCertificates.certificateOnlyPkcs12(), charArrayOf(), "client")
        }.message shouldContain "does not refer to a private key"
    }

    test("rejects expired certificate") {
        val expired = TestCertificates.expiredClientCertificate()

        shouldThrow<CertificateMaterialException> {
            loader.load(
                TestCertificates.pkcs12(client = expired, password = password, alias = alias),
                password,
                alias
            )
        }.message shouldContain "expired certificate"
    }
})
