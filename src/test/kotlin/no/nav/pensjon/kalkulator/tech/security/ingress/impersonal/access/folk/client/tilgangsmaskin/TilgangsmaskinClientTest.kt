package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.TilgangResult
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeNoContentResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper

class TilgangsmaskinClientTest : ShouldSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    fun client(context: BeanFactory, jsonMapper: JsonMapper) =
        TilgangsmaskinClient(
            baseUrl!!,
            webClientBuilder = context.getBean<WebClient.Builder>(),
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

    should("return innvilget when service returns 204 No Content") {
        server?.arrangeNoContentResponse()

        Arrange.webClientContextRunner().run {
            val result = client(context = it, jsonMapper).sjekkTilgang(pid)
            with(result) {
                innvilget shouldBe true
                avvisningAarsak shouldBe null
            }
        }
    }

    should("return avvist with details when service returns 403 Forbidden") {
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

        server?.arrangeJsonResponse(status = HttpStatus.FORBIDDEN, body = problemDetailJson)

        Arrange.webClientContextRunner().run {
            client(context = it, jsonMapper).sjekkTilgang(pid) shouldBe TilgangResult(
                innvilget = false,
                avvisningAarsak = AvvisningAarsak.GEOGRAFISK,
                begrunnelse = "Veileder har ikke tilgang til bruker i denne geografiske enheten",
                traceId = "trace-123"
            )
        }
    }
})

private val jsonMapper: JsonMapper =
    JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()
