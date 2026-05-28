package no.nav.pensjon.kalkulator.vedtak.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import no.nav.pensjon.kalkulator.vedtak.InformasjonOmAvdoed
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class PenLoependeVedtakClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        PenLoependeVedtakClient(
            baseUrl!!,
            webClientBuilder = context.getBean<WebClient.Builder>(),
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

    test("hent løpende vedtak med alderspensjon") {
        server?.arrangeOkJsonResponse(VEDTAK_AP)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

            with(response) {
                with(loependeAlderspensjon!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2025-10-01"
                    sivilstatus shouldBe Sivilstatus.UGIFT
                }
                ufoeretrygd shouldBe null
                privatAfp shouldBe null
            }
        }
    }

    test("hent løpende vedtak med uføre") {
        server?.arrangeOkJsonResponse(VEDTAK_UFOERE)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

            with(response) {
                loependeAlderspensjon shouldBe null
                with(ufoeretrygd!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2022-07-01"
                }
                privatAfp shouldBe null
            }
        }
    }

    test("hent løpende vedtak med alderspensjon og uføre") {
        server?.arrangeOkJsonResponse(VEDTAK_AP_OG_UFOERE)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

            with(response) {
                with(loependeAlderspensjon!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2025-10-01"
                    sivilstatus shouldBe Sivilstatus.GIFT
                }
                with(ufoeretrygd!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2022-07-01"
                }
                privatAfp shouldBe null
            }
        }
    }

    test("hent løpende vedtak med alderspensjon, uføre og privat AFP") {
        server?.arrangeOkJsonResponse(VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

            with(response) {
                with(loependeAlderspensjon!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2025-10-01"
                    sivilstatus shouldBe Sivilstatus.SKILT
                }
                with(ufoeretrygd!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2022-07-01"
                }
                privatAfp?.fom.toString() shouldBe "2022-07-01"
            }
        }
    }

    test("hent løpende vedtak med alderspensjon, uføre, privat AFP og tidsbegrenset offentlig AFP") {
        server?.arrangeOkJsonResponse(VEDTAK_AP_OG_UFOERE_OG_PRIVAT_AFP_OG_PRE2025_OFFENTLIG_AFP)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

            with(response) {
                with(loependeAlderspensjon!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2025-10-01"
                    sivilstatus shouldBe Sivilstatus.ENKE_ELLER_ENKEMANN
                }
                with(ufoeretrygd!!) {
                    grad shouldBe 100
                    fom.toString() shouldBe "2022-07-01"
                }
                privatAfp?.fom.toString() shouldBe "2022-07-01"
            }
        }
    }

    test("hent løpende vedtak uten vedtak") {
        server?.arrangeOkJsonResponse(VEDTAK_INGEN)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).hentLoependeVedtak(pid)

            with(response) {
                loependeAlderspensjon shouldBe null
                ufoeretrygd shouldBe null
                privatAfp shouldBe null
            }
        }
    }

    test("informasjon om avdød") {
        server?.arrangeOkJsonResponse(INFORMASJON_OM_AVDOED)

        Arrange.webClientContextRunner().run {
            client(context = it).hentLoependeVedtak(pid).avdoed shouldBe InformasjonOmAvdoed(
                pid = pid,
                doedsdato = LocalDate.of(2023, 10, 13),
                foersteAlderspensjonVirkningsdato = LocalDate.of(2024, 3, 1),
                aarligPensjonsgivendeInntektErMinst1G = true,
                harTilstrekkeligMedlemskapIFolketrygden = false,
                antallAarUtenlands = 4,
                erFlyktning = true
            )
        }
    }

    test("hentLoependeVedtak should throw EgressException when response is 'Not Found'") {
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
private const val INFORMASJON_OM_AVDOED = """
{"alderspensjon":null,"ufoeretrygd":null,"afpPrivat":null,"afp":null,"avdoed":{"pid":"12906498357","doedsdato":"2023-10-13","foersteVirkningsdato":"2024-03-01","aarligPensjonsgivendeInntektErMinst1G":true,"harTilstrekkeligMedlemskapIFolketrygden":false,"antallAarUtenlands":4,"erFlyktning":true}}
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
