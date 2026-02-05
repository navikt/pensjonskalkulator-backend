package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.pensjon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.TilgangsmaskinenClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.json.JsonMapper

class TilgangsmaskinenClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    fun client(context: BeanFactory, jsonMapper: JsonMapper) =
        TilgangsmaskinenClient(
            baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            traceAid,
            jsonMapper,
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

    test("sjekkTilgang should return innvilget when service returns 200 OK") {
        server?.enqueue(
            MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
        )

        val jsonMapper = mockk<JsonMapper>(relaxed = true)

        Arrange.webClientContextRunner().run {
            val result = client(context = it, jsonMapper).sjekkTilgang(pid)
            result.innvilget shouldBe true
            result.avvisningAarsak shouldBe null
        }
    }

    test("sjekkTilgang should return avvist with details when service returns 403") {
        val problemDetailJson = """
            {
                "type": "urn:tilgangsmaskin:avvist",
                "title": "AVVIST_GEOGRAFISK",
                "status": 403,
                "instance": "/api/v1/komplett",
                "brukerIdent": "12345678901",
                "navIdent": "Z123456",
                "begrunnelse": "Veileder har ikke tilgang til bruker i denne geografiske enheten",
                "traceId": "trace-123",
                "kanOverstyres": true
            }
        """.trimIndent()

        server?.enqueue(
            MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.FORBIDDEN.value())
                .setBody(problemDetailJson)
        )

        val jsonMapper = JsonMapper.builder()
            .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

        Arrange.webClientContextRunner().run {
            val result = client(context = it, jsonMapper).sjekkTilgang(pid)
            result.innvilget shouldBe false
            result.avvisningAarsak shouldBe AvvisningAarsak.GEOGRAFISK
            result.begrunnelse shouldBe "Veileder har ikke tilgang til bruker i denne geografiske enheten"
            result.traceId shouldBe "trace-123"
        }
    }
})
