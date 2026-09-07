package no.nav.pensjon.kalkulator.tech.ssl

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration
import org.springframework.boot.ssl.SslBundles
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import java.util.function.Supplier

class NorskPensjonSslConfigurationTest : FunSpec({

    test("loads secret once and registers norsk-pensjon SSL bundle at startup") {
        val credentialsResourceName = "projects/project/secrets/credentials/versions/1"
        val keyStoreResourceName = "projects/project/secrets/certificate/versions/1"
        val password = "password".toCharArray()
        val alias = "nav integrasjon norsk pensjon"
        val payloadReader = mockk<SecretPayloadReader> {
            io.mockk.every { read(credentialsResourceName) } returns
                """{"password":"password","alias":"$alias","type":"pkcs12"}""".toByteArray()
            io.mockk.every { read(keyStoreResourceName) } returns
                TestCertificates.pkcs12(password = password, alias = alias)
        }

        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SslAutoConfiguration::class.java))
            .withUserConfiguration(NorskPensjonSslConfiguration::class.java)
            .withBean(SecretManagerServiceClient::class.java, Supplier { mockk(relaxed = true) })
            .withBean(SecretPayloadReader::class.java, Supplier { payloadReader })
            .withBean(ObjectMapper::class.java, Supplier { JsonMapper.builder().build() })
            .withPropertyValues(
                "norsk-pensjon.mtls.enabled=true",
                "norsk-pensjon.mtls.secret.project-id=project",
                "norsk-pensjon.mtls.secret.credentials-secret-id=credentials",
                "norsk-pensjon.mtls.secret.credentials-version=1",
                "norsk-pensjon.mtls.secret.key-store-secret-id=certificate",
                "norsk-pensjon.mtls.secret.key-store-version=1"
            )
            .run { context ->
                context.startupFailure shouldBe null
                context.getBean(SslBundles::class.java)
                    .getBundle("norsk-pensjon")
                    .stores
                    .keyStore
                    ?.isKeyEntry(alias) shouldBe true
                verify(exactly = 1) { payloadReader.read(credentialsResourceName) }
                verify(exactly = 1) { payloadReader.read(keyStoreResourceName) }
            }
    }
})
