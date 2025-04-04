package no.nav.pensjon.kalkulator.vedtak.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Sivilstand
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
            with(alderspensjon!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2025-10-01"
                sivilstand shouldBe Sivilstand.UGIFT
            }
            ufoeretrygd shouldBe null
            afpPrivat shouldBe null
            afpOffentlig shouldBe null
        }
    }

    @Test
    fun `hent loepende vedtak med ufoere`() {
        arrange(vedtakUfoereResponse())

        val response = client.hentLoependeVedtak(pid)

        with(response) {
            alderspensjon shouldBe null
            with(ufoeretrygd!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2022-07-01"
            }
            afpPrivat shouldBe null
            afpOffentlig shouldBe null
        }
    }

    @Test
    fun `hent loepende vedtak med alderspensjon og ufoere`() {
        arrange(vedtakApOgUfoereResponse())

        val response = client.hentLoependeVedtak(pid)

        with(response) {
            with(alderspensjon!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2025-10-01"
                sivilstand shouldBe Sivilstand.GIFT
            }
            with(ufoeretrygd!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2022-07-01"
            }
            afpPrivat shouldBe null
            afpOffentlig shouldBe null
        }
    }

    @Test
    fun `hent loepende vedtak med alderspensjon, ufoere og privat AFP`() {
        arrange(vedtakApOgUfoereOgPrivatAfpResponse())

        val response = client.hentLoependeVedtak(pid)

        with(response) {
            with(alderspensjon!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2025-10-01"
                sivilstand shouldBe Sivilstand.SKILT
            }
            with(ufoeretrygd!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2022-07-01"
            }
            afpPrivat?.fom.toString() shouldBe "2022-07-01"
            afpOffentlig shouldBe null
        }
    }

    @Test
    fun `hent loepende vedtak med alderspensjon, ufoere, privat AFP og pre-2025 offentlig AFP`() {
        arrange(vedtakApOgUfoereOgPrivatAfpOgPre2025OffentligAfpResponse())

        val response = client.hentLoependeVedtak(pid)

        with(response) {
            with(alderspensjon!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2025-10-01"
                sivilstand shouldBe Sivilstand.ENKE_ELLER_ENKEMANN
            }
            with(ufoeretrygd!!) {
                grad shouldBe 100
                fom.toString() shouldBe "2022-07-01"
            }
            afpPrivat?.fom.toString() shouldBe "2022-07-01"
            afpOffentlig shouldBe null
        }
    }

    @Test
    fun `hent loepende vedtak med ingen vedtak`() {
        arrange(vedtakIngenResponse())

        val response = client.hentLoependeVedtak(pid)

        with(response) {
            alderspensjon shouldBe null
            ufoeretrygd shouldBe null
            afpPrivat shouldBe null
            afpOffentlig shouldBe null
        }
    }

    @Test
    fun `hentLoependeVedtak throws EgressException when response is '404 Not Found'`() {
        arrange(notFoundResponse())

        val exception = shouldThrow<EgressException> { client.hentLoependeVedtak(pid) }

        with(exception) {
            message shouldBe """{
    "feilmelding": "Personen med fødselsnummer ${pid.value} finnes ikke i den lokale oversikten over personer. (PEN029)",
    "merknader": []
}"""
            exception.isClientError shouldBe true
        }
    }

    @Test
    fun `hentLoependeVedtak faar en Internal Server Error`() {
        arrange(serverErrorResponse())
        arrange(serverErrorResponse()) // 1 retry

        val exception = shouldThrow<EgressException> { client.hentLoependeVedtak(pid) }

        with(exception) {
            message shouldBe "Failed calling /api/simulering/vedtak/loependevedtak"
            exception.isClientError shouldBe false
        }
    }

    private companion object {
        @Language("json")
        private const val VEDTAK_AP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"UGIF","sivilstatus":"UGIF"},"ufoeretrygd":null,"afpPrivat":null,"afp":null}
"""

        @Language("json")
        private const val VEDTAK_UFOERE = """
{"alderspensjon":null,"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":null,"afp":null}
"""

        @Language("json")
        private const val VEDTAK_AP_OG_UFOERE = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"GIFT","sivilstatus":"GIFT"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":null,"afp":null}
"""

        @Language("json")
        private const val VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"SKIL","sivilstatus":"SKIL"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":{"grad":100,"fraOgMed":"2022-07-01"},"afp":null}
"""

        @Language("json")
        private const val VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP_OG_PRE2025_OFFENTLIG_AFP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"ENKE","sivilstatus":"ENKE"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":{"grad":100,"fraOgMed":"2022-07-01"},"afp":{"grad":100,"fraOgMed":"2022-07-01"}}
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
    "path": "/api/simulering/vedtak/loependevedtak"
}"""

        private fun vedtakApResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP)
        private fun vedtakUfoereResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_UFOERE)
        private fun vedtakApOgUfoereResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP_OG_UFOERE)

        private fun vedtakApOgUfoereOgPrivatAfpResponse() =
            jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP)

        private fun vedtakApOgUfoereOgPrivatAfpOgPre2025OffentligAfpResponse() =
            jsonResponse(HttpStatus.OK).setBody(VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP_OG_PRE2025_OFFENTLIG_AFP)

        private fun vedtakIngenResponse() = jsonResponse(HttpStatus.OK).setBody(VEDTAK_INGEN)

        private fun notFoundResponse() = jsonResponse(HttpStatus.NOT_FOUND).setBody(PERSON_NOT_FOUND)

        private fun serverErrorResponse() = jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody(PEN_ERROR)
    }
}
