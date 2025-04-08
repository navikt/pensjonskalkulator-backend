package no.nav.pensjon.kalkulator.omstillingsstoenad.client

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.WebClientTestConfig
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.TestObjects.jwt
import no.nav.pensjon.kalkulator.mock.TestObjects.pid1
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class EtterlatteOmstillingsstoenadClientTest : FunSpec({
    var server: MockWebServer? = null
    var baseUrl: String? = null
    val dato = LocalDate.of(2025, 1, 1)

    beforeSpec {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

        SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
            initialAuth = TestingAuthenticationToken("TEST_USER", jwt),
            egressTokenSuppliersByService = EgressTokenSuppliersByService(mapOf()),
            target = RepresentasjonTarget(pid = pid1, rolle = RepresentertRolle.FULLMAKT_GIVER)
        )

        server = MockWebServer().also { it.start() }
        baseUrl = server.let { "http://localhost:${it.port}" }
    }

    afterSpec {
        server?.shutdown()
    }

    test("omstillinsstoenadClient sender foedselsnummer i body + returnerer false naar stoenad ikke mottas") {
        val contextRunner = ApplicationContextRunner().withConfiguration(
            AutoConfigurations.of(WebClientTestConfig::class.java)
        )

        server?.enqueue(
            MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(statusResponseBody(false).trimIndent())
        )

        contextRunner.run {
            val client = EtterlatteOmstillingsstoenadClient(
                baseUrl = baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid = mock(TraceAid::class.java),
                retryAttempts = "0"
            )

            runTest {
                client.mottarOmstillingsstoenad(pid, paaDato = dato) shouldBe false
                server?.takeRequest()?.body?.readUtf8() shouldBe """{"foedselsnummer":"12906498357"}"""
            }
        }
    }

    test("omstillinsstoenadClient gjentar request ved serverfeil + returnerer true naar stoenad mottas") {
        val contextRunner = ApplicationContextRunner().withConfiguration(
            AutoConfigurations.of(WebClientTestConfig::class.java)
        )

        server?.enqueue( // first attempt
            MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Feil")
        )
        server?.enqueue( // second attempt
            MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(statusResponseBody(true).trimIndent())
        )

        contextRunner.run {
            val client = EtterlatteOmstillingsstoenadClient(
                baseUrl = baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid = mock(TraceAid::class.java),
                retryAttempts = "1"
            )

            runTest {
                client.mottarOmstillingsstoenad(pid, paaDato = dato) shouldBe true
            }
        }
    }
})

private fun statusResponseBody(value: Boolean) =
    """{
    "omstillingsstoenad": $value
}"""
