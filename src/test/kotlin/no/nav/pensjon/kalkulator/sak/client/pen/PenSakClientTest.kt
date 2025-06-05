package no.nav.pensjon.kalkulator.sak.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.sak.SakStatus
import no.nav.pensjon.kalkulator.sak.SakType
import no.nav.pensjon.kalkulator.sak.client.pen.PenSakClientTestObjects.PEN_ERROR
import no.nav.pensjon.kalkulator.sak.client.pen.PenSakClientTestObjects.PEN_SAK
import no.nav.pensjon.kalkulator.sak.client.pen.PenSakClientTestObjects.PERSON_NOT_FOUND
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

class PenSakClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    fun client(context: BeanFactory) =
        PenSakClient(
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

    test("fetchSaker returns status and type") {
        server?.arrangeOkJsonResponse(PEN_SAK)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).fetchSaker(pid)

            with(response[0]) {
                status shouldBe SakStatus.LOEPENDE
                type shouldBe SakType.UFOERETRYGD
            }
        }
    }

    test("fetchSaker should throw EgressException when response is 'Not Found'`") {
        server?.arrangeResponse(HttpStatus.NOT_FOUND, PERSON_NOT_FOUND)

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> { client(context = it).fetchSaker(pid) }

            with(exception) {
                message shouldBe """{
    "feilmelding": "Personen med fødselsnummer ${pid.value} finnes ikke i den lokale oversikten over personer. (PEN029)",
    "merknader": []
}"""
                isClientError shouldBe true
            }
        }
    }

    test("fetchSaker should throw EgressException when response is 'Internal Server Error'") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, PEN_ERROR)
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, PEN_ERROR) // for retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> { client(context = it).fetchSaker(pid) }

            with(exception) {
                message shouldBe "Failed calling /api/sak/sammendrag"
                isClientError shouldBe false
            }
        }
    }
})

private object PenSakClientTestObjects {

    @Language("json")
    const val PEN_SAK = """[
    {
        "sakId": 77957857,
        "sakType": "UFOREP",
        "sakStatus": "LOPENDE",
        "fomDato": "2021-05-01T00:00:00+0200",
        "tomDato": null,
        "enhetId": "4410",
        "arkivtema": "UFO"
    }
]"""

    @Language("json")
    const val PERSON_NOT_FOUND = """{
    "feilmelding": "Personen med fødselsnummer 12906498357 finnes ikke i den lokale oversikten over personer. (PEN029)",
    "merknader": []
}"""

    @Language("json")
    const val PEN_ERROR = """{
    "timestamp": "2023-10-13T10:38:43+0200",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/api/sak/sammendrag"
}"""
}
