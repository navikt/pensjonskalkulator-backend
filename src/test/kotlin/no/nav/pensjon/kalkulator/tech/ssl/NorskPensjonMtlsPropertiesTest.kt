package no.nav.pensjon.kalkulator.tech.ssl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NorskPensjonMtlsPropertiesTest : FunSpec({

    test("builds full Secret Manager resource name") {
        NorskPensjonMtlsProperties.Secret().apply {
            projectId = "team-project"
            credentialsSecretId = "norsk-pensjon-credentials"
            credentialsVersion = "42"
        }.credentialsResourceName() shouldBe
            "projects/team-project/secrets/norsk-pensjon-credentials/versions/42"
    }

    test("builds PKCS12 Secret Manager resource name") {
        NorskPensjonMtlsProperties.Secret().apply {
            projectId = "team-project"
            keyStoreSecretId = "norsk-pensjon-certificate"
            keyStoreVersion = "42"
        }.keyStoreResourceName() shouldBe
            "projects/team-project/secrets/norsk-pensjon-certificate/versions/42"
    }

    test("rejects incomplete resource name") {
        val secret = NorskPensjonMtlsProperties.Secret().apply { projectId = "" }

        shouldThrow<IllegalArgumentException> { secret.credentialsResourceName() }
    }
})
