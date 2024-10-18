package no.nav.pensjon.kalkulator.vedtak.client.pen

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PenLoependeVedtakClientTest : WebClientTest() {

    private lateinit var client: PenLoependeVedtakClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = PenLoependeVedtakClient(baseUrl(), webClientBuilder, traceAid, "1")
        arrangeSecurityContext()
    }

    @Test
    fun `hent loepende vedtak med alderspensjon`() {
        arrange(vedtakApResponse())
        val response = client.hentLoependeVedtak(pid)
        with(response) {
            assertEquals(100, alderspensjon?.grad)
            assertEquals("2025-10-01", alderspensjon?.fom.toString())
            assertEquals(null, ufoeretrygd)
            assertEquals(null, afpPrivat)
            assertEquals(null, afpOffentlig)
        }
    }

    @Test
    fun `hent loepende vedtak med ufoere`() {
        arrange(vedtakUforeResponse())
        val response = client.hentLoependeVedtak(pid)
        with(response) {
            assertEquals(null, alderspensjon)
            assertEquals(100, ufoeretrygd?.grad)
            assertEquals("2022-07-01", ufoeretrygd?.fom.toString())
            assertEquals(null, afpPrivat)
            assertEquals(null, afpOffentlig)
        }
    }

    @Test
    fun `hent loepende vedtak med alderspensjon og ufoere`() {
        arrange(vedtakApOgUforeResponse())
        val response = client.hentLoependeVedtak(pid)
        with(response) {
            assertEquals(100, alderspensjon?.grad)
            assertEquals("2025-10-01", alderspensjon?.fom.toString())
            assertEquals(100, ufoeretrygd?.grad)
            assertEquals("2022-07-01", ufoeretrygd?.fom.toString())
            assertEquals(null, afpPrivat)
            assertEquals(null, afpOffentlig)
        }
    }

    @Test
    fun `hent loepende vedtak med alderspensjon, ufoere og afpPrivat`() {
        arrange(vedtakApOgUforeOgAfpPrivatResponse())
        val response = client.hentLoependeVedtak(pid)
        with(response) {
            assertEquals(100, alderspensjon?.grad)
            assertEquals("2025-10-01", alderspensjon?.fom.toString())
            assertEquals(100, ufoeretrygd?.grad)
            assertEquals("2022-07-01", ufoeretrygd?.fom.toString())
            assertEquals("2022-07-01", afpPrivat?.fom.toString())
            assertEquals(null, afpOffentlig)
        }
    }

    @Test
    fun `hent loepende vedtak med alderspensjon, ufoere, afpPrivat og gammel afpOffentlig`() {
        arrange(vedtakApOgUforeOgAfpPrivatOgAfpResponse())
        val response = client.hentLoependeVedtak(pid)
        with(response) {
            assertEquals(100, alderspensjon?.grad)
            assertEquals("2025-10-01", alderspensjon?.fom.toString())
            assertEquals(100, ufoeretrygd?.grad)
            assertEquals("2022-07-01", ufoeretrygd?.fom.toString())
            assertEquals("2022-07-01", afpPrivat?.fom.toString())
            assertEquals(null, afpOffentlig)
        }
    }

    @Test
    fun `hent loepende vedtak med ingen vedtak`() {
        arrange(vedtakIngenResponse())
        val response = client.hentLoependeVedtak(pid)
        with(response) {
            assertEquals(null, alderspensjon)
            assertEquals(null, ufoeretrygd)
            assertEquals(null, afpPrivat)
            assertEquals(null, afpOffentlig)
        }
    }

    @Test
    fun `hentLoependeVedtak throws EgressException when response is '404 Not Found'`() {
        arrange(notFoundResponse())

        val exception = assertThrows<EgressException> { client.hentLoependeVedtak(pid) }

        assertEquals(
            """{
    "feilmelding": "Personen med fødselsnummer ${pid.value} finnes ikke i den lokale oversikten over personer. (PEN029)",
    "merknader": []
}""", exception.message
        )
        assertTrue(exception.isClientError)
    }

    @Test
    fun `hentLoependeVedtak faar en Internal Server Error`() {
        arrange(serverErrorResponse())
        arrange(serverErrorResponse()) // 1 retry

        val exception = assertThrows<EgressException> { client.hentLoependeVedtak(pid) }

        assertEquals("Failed calling /pen/api/simulering/vedtak/loependevedtak", exception.message)
        assertFalse(exception.isClientError)
    }

    private companion object {
        @Language("json")
        private const val VEDTAK_AP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01"},"ufoeretrygd":null,"afpPrivat":null,"afp":null}
"""
        @Language("json")
        private const val VEDTAK_UFORE = """
{"alderspensjon":null,"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":null,"afp":null}
"""
        @Language("json")
        private const val VEDTAK_AP_OG_UFORE = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":null,"afp":null}
"""
        @Language("json")
        private const val VEDTAK_AP_OG_UFORE_OG_AFPPRIVAT = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":{"grad":100,"fraOgMed":"2022-07-01"},"afp":null}
"""
        @Language("json")
        private const val VEDTAK_AP_OG_UFORE_OG_AFPPRIVAT_OG_AFP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":{"grad":100,"fraOgMed":"2022-07-01"},"afp":{"grad":100,"fraOgMed":"2022-07-01"}}
"""
        @Language("json")
        private const val VEDTAK_INGEN = """
{"alderspensjon":null,"ufoeretrygd":null,"afpPrivat":null,"afp":null}
"""

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
    "path": "/pen/api/simulering/vedtak/loependevedtak"
}"""

        private fun vedtakApResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP)
        private fun vedtakUforeResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_UFORE)
        private fun vedtakApOgUforeResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP_OG_UFORE)
        private fun vedtakApOgUforeOgAfpPrivatResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP_OG_UFORE_OG_AFPPRIVAT)
        private fun vedtakApOgUforeOgAfpPrivatOgAfpResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP_OG_UFORE_OG_AFPPRIVAT_OG_AFP)
        private fun vedtakIngenResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_INGEN)

        private fun notFoundResponse() = jsonResponse(HttpStatus.NOT_FOUND).setBody(PERSON_NOT_FOUND)

        private fun serverErrorResponse() = jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody(PEN_ERROR)
    }
}