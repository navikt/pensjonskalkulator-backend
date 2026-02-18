package no.nav.pensjon.kalkulator.tech.crypto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.crypto.client.CryptoClient

class CryptoServiceTest : ShouldSpec({

    should("invoke client for encryption") {
        CryptoService(cryptoClient = arrangeClient()).encrypt("abc") shouldBe "xyz"
    }

    should("invoke client for decryption") {
        CryptoService(cryptoClient = arrangeClient()).decrypt("xyz") shouldBe "abc"
    }
})

private fun arrangeClient(): CryptoClient =
    mockk {
        every { encrypt("abc") } returns "xyz"
        every { decrypt("xyz") } returns "abc"
    }
