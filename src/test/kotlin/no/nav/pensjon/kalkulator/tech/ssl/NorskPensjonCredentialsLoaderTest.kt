package no.nav.pensjon.kalkulator.tech.ssl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import tools.jackson.databind.json.JsonMapper

class NorskPensjonCredentialsLoaderTest : FunSpec({
    val loader = NorskPensjonCredentialsLoader(JsonMapper.builder().build())

    test("loads password and alias from credentials JSON") {
        val credentials = loader.load(
            """{"password":"secret","alias":"nav integrasjon norsk pensjon","type":"pkcs12"}""".toByteArray()
        )

        String(credentials.password) shouldBe "secret"
        credentials.alias shouldBe "nav integrasjon norsk pensjon"
        credentials.toString() shouldBe
            "NorskPensjonKeyStoreCredentials(password=******, alias=nav integrasjon norsk pensjon)"
    }

    test("rejects unsupported credentials type") {
        shouldThrow<CertificateMaterialException> {
            loader.load("""{"password":"secret","alias":"alias","type":"jks"}""".toByteArray())
        }.message shouldContain "type must be 'pkcs12'"
    }

    test("rejects missing password") {
        shouldThrow<CertificateMaterialException> {
            loader.load("""{"alias":"alias","type":"pkcs12"}""".toByteArray())
        }.message shouldContain "does not contain a password"
    }

    test("rejects malformed JSON without exposing contents") {
        shouldThrow<CertificateMaterialException> {
            loader.load("""{"password":"sensitive"""".toByteArray())
        }.message shouldBe "Norsk Pensjon credentials secret does not contain valid JSON"
    }
})
