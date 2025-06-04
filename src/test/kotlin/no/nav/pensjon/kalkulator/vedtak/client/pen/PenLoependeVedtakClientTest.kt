package no.nav.pensjon.kalkulator.vedtak.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.PEN_ERROR
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.PERSON_NOT_FOUND
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.VEDTAK_AP
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.VEDTAK_AP_OG_UFOERE
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP_OG_PRE2025_OFFENTLIG_AFP
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.VEDTAK_INGEN
import no.nav.pensjon.kalkulator.vedtak.client.pen.PenLoependeVedtakClientTestObjects.VEDTAK_UFOERE
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

class PenLoependeVedtakClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        PenLoependeVedtakClient(
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

    test("hent løpende vedtak med alderspensjon`") {
        server?.arrangeOkJsonResponse(VEDTAK_AP)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

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
    }

    test("hent løpende vedtak med ufoere`") {
        server?.arrangeOkJsonResponse(VEDTAK_UFOERE)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

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
    }

    test("hent løpende vedtak med alderspensjon og ufoere`") {
        server?.arrangeOkJsonResponse(VEDTAK_AP_OG_UFOERE)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

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
    }

    test("hent løpende vedtak med alderspensjon, ufoere og privat AFP`") {
        server?.arrangeOkJsonResponse(VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

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
    }

    test("hent løpende vedtak med alderspensjon, ufoere, privat AFP og pre-2025 offentlig AFP`") {
        server?.arrangeOkJsonResponse(VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP_OG_PRE2025_OFFENTLIG_AFP)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

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
    }

    test("hent løpende vedtak med ingen vedtak`") {
        server?.arrangeOkJsonResponse(VEDTAK_INGEN)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

            with(response) {
                alderspensjon shouldBe null
                ufoeretrygd shouldBe null
                afpPrivat shouldBe null
                afpOffentlig shouldBe null
            }
        }
    }

    test("hentLoependeVedtak should throw EgressException when response is 'Not Found'`") {
        server?.arrangeResponse(HttpStatus.NOT_FOUND, PERSON_NOT_FOUND)

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> { client(context = it).hentLoependeVedtak(pid) }

            with(exception) {
                message shouldBe """{
    "feilmelding": "Personen med fødselsnummer ${pid.value} finnes ikke i den lokale oversikten over personer. (PEN029)",
    "merknader": []
}"""
                exception.isClientError shouldBe true
            }
        }
    }

    test("hentLoependeVedtak should throw EgressException when response is 'Internal Server Error'") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, PEN_ERROR)
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, PEN_ERROR) // for retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> { client(context = it).hentLoependeVedtak(pid) }

            with(exception) {
                message shouldBe "Failed calling /api/simulering/vedtak/loependevedtak"
                exception.isClientError shouldBe false
            }
        }
    }
})

object PenLoependeVedtakClientTestObjects {
    @Language("json")
    const val VEDTAK_AP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"UGIF","sivilstatus":"UGIF"},"ufoeretrygd":null,"afpPrivat":null,"afp":null}
"""

    @Language("json")
    const val VEDTAK_UFOERE = """
{"alderspensjon":null,"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":null,"afp":null}
"""

    @Language("json")
    const val VEDTAK_AP_OG_UFOERE = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"GIFT","sivilstatus":"GIFT"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":null,"afp":null}
"""

    @Language("json")
    const val VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"SKIL","sivilstatus":"SKIL"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":{"grad":100,"fraOgMed":"2022-07-01"},"afp":null}
"""

    @Language("json")
    const val VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP_OG_PRE2025_OFFENTLIG_AFP = """
{"alderspensjon":{"grad":100,"fraOgMed":"2025-10-01","sivilstand":"ENKE","sivilstatus":"ENKE"},"ufoeretrygd":{"grad":100,"fraOgMed":"2022-07-01"},"afpPrivat":{"grad":100,"fraOgMed":"2022-07-01"},"afp":{"grad":100,"fraOgMed":"2022-07-01"}}
"""

    @Language("json")
    const val VEDTAK_INGEN = """
{"alderspensjon":null,"ufoeretrygd":null,"afpPrivat":null,"afp":null}
"""

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
    "path": "/api/simulering/vedtak/loependevedtak"
}"""
}
