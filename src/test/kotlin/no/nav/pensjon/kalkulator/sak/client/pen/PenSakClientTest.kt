package no.nav.pensjon.kalkulator.sak.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.sak.SakStatus
import no.nav.pensjon.kalkulator.sak.SakType
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PenSakClientTest : WebClientTest() {

    private lateinit var client: PenSakClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = PenSakClient(baseUrl(), webClientBuilder, traceAid, "1")
        arrangeSecurityContext()
    }

    @Test
    fun `fetchSaker returns status and type`() {
        arrange(okResponse())

        val response = client.fetchSaker(pid)

        with(response[0]) {
            status shouldBe SakStatus.LOEPENDE
            type shouldBe SakType.UFOERETRYGD
        }
    }

    @Test
    fun `fetchSaker throws EgressException when response is '404 Not Found'`() {
        arrange(notFoundResponse())

        val exception = shouldThrow<EgressException> { client.fetchSaker(pid) }

        with(exception) {
            message shouldBe """{
    "feilmelding": "Personen med fødselsnummer ${pid.value} finnes ikke i den lokale oversikten over personer. (PEN029)",
    "merknader": []
}"""
            isClientError shouldBe true
        }
    }

    @Test
    fun `fetchSaker throws EgressException when response is '500 Internal Server Error'`() {
        arrange(serverErrorResponse())
        arrange(serverErrorResponse()) // 1 retry

        val exception = shouldThrow<EgressException> { client.fetchSaker(pid) }

        with(exception) {
            message shouldBe "Failed calling /api/sak/sammendrag"
            isClientError shouldBe false
        }
    }

    private companion object {

        @Language("json")
        private const val PEN_SAK = """[
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
        private const val PERSON_NOT_FOUND = """{
    "feilmelding": "Personen med fødselsnummer 12906498357 finnes ikke i den lokale oversikten over personer. (PEN029)",
    "merknader": []
}"""

        @Language("json")
        private const val PEN_ERROR = """{
    "timestamp": "2023-10-13T10:38:43+0200",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/api/sak/sammendrag"
}"""

        private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(PEN_SAK)

        private fun notFoundResponse() = jsonResponse(HttpStatus.NOT_FOUND).setBody(PERSON_NOT_FOUND)

        private fun serverErrorResponse() = jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody(PEN_ERROR)
    }
}
