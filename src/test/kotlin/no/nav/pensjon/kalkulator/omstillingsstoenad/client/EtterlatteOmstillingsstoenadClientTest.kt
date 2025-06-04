package no.nav.pensjon.kalkulator.omstillingsstoenad.client

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class EtterlatteOmstillingsstoenadClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val dato = LocalDate.of(2025, 1, 1)
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        EtterlatteOmstillingsstoenadClient(
            baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            traceAid,
            retryAttempts = "1"
        )

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("'mottarOmstillingsstoenad' sender foedselsnummer i body + returnerer false naar stoenad ikke mottas") {
        server?.arrangeOkJsonResponse(statusResponseBody(false).trimIndent())

        Arrange.webClientContextRunner().run {
            runTest {
                client(context = it).mottarOmstillingsstoenad(pid, paaDato = dato) shouldBe false
                server?.takeRequest()?.body?.readUtf8() shouldBe """{"foedselsnummer":"12906498357"}"""
            }
        }
    }

    test("'mottarOmstillingsstoenad' gjentar request ved serverfeil + returnerer true naar stoenad mottas") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil") // first attempt
        server?.arrangeOkJsonResponse(statusResponseBody(true).trimIndent()) // second attempt

        Arrange.webClientContextRunner().run {
            runTest {
                client(context = it).mottarOmstillingsstoenad(pid, paaDato = dato) shouldBe true
            }
        }
    }
})

private fun statusResponseBody(value: Boolean) =
    """{
    "omstillingsstoenad": $value
}"""
