package no.nav.pensjon.kalkulator.ufoere.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.DateFactory.date
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import no.nav.pensjon.kalkulator.ufoere.Sakstype
import no.nav.pensjon.kalkulator.ufoere.client.pen.PenVedtakClientTestObjects.RESPONSE
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream

class PenVedtakClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        PenVedtakClient(
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

    test("bestemGjeldendeVedtak uses supplied PID in request and returns sakstype") {
        server!!.arrangeOkJsonResponse(RESPONSE)

        Arrange.webClientContextRunner().run {

            val vedtaksliste = client(context = it).bestemGjeldendeVedtak(pid, date)

            ByteArrayOutputStream().use {
                server.takeRequest().apply {
                    getHeader("fnr") shouldBe pid.value
                    requestUrl?.queryParameter("fom") shouldBe "2023-04-05"
                }
            }
            vedtaksliste[0].sakstype shouldBe Sakstype.UFOEREPENSJON
        }
    }

    test("bestemGjeldendeVedtak retries in case of server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeOkJsonResponse(RESPONSE)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).bestemGjeldendeVedtak(pid, date)
            response[0].sakstype shouldBe Sakstype.UFOEREPENSJON
        }
    }

    test("bestemGjeldendeVedtak does not retry in case of client error") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, "My bad")
        // No 2nd response arranged, since no retry

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> {
                client(context = it).bestemGjeldendeVedtak(pid, date)
            }.message shouldBe "My bad"
        }
    }

    test("bestemGjeldendeVedtak handles server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil") // for retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> { client(context = it).bestemGjeldendeVedtak(pid, date) }

            with(exception) {
                message shouldBe "Failed calling /api/vedtak/bestemgjeldende?fom=2023-04-05"
                (cause as EgressException).message shouldBe "Feil"
            }
        }
    }
})

object PenVedtakClientTestObjects {

    @Language("JSON")
    const val RESPONSE = """[
{
    "vedtakId": 42805245,
    "vedtakstype": "REGULERING",
    "vedtakstatus": "IVERKS",
    "sakId": 22957857,
    "sakstype": "UFOREP",
    "kravhodeId": 43062974,
    "gjelderPerson": {
        "penPersonId": 23567662,
        "fnr": "21867599476",
        "fodselsdato": "1975-06-21",
        "dodsdato": null
    },
    "vilkarsvedtakListe": [],
    "ansvarligSaksbehandler": "BPEN068",
    "attesterer": "BPEN068",
    "attestertDato": "2023-06-13T00:00:00+0200",
    "vedtaksdato": "2023-06-13T00:00:00+0200",
    "tilIverksettelseDato": null,
    "sendtSamordningDato": null,
    "samordnetDato": null,
    "stoppetDato": null,
    "reaktivisertDato": null,
    "iverksattDato": "2023-06-13T00:00:00+0200",
    "virkFom": "2023-05-01T00:00:00+0200",
    "virkTom": null,
    "lopendeFom": "2023-05-01T00:00:00+0200",
    "lopendeTom": null,
    "gjelderFom": "2023-05-01T00:00:00+0200",
    "gjelderTom": null,
    "forrigeVedtak": 42803157,
    "basertPaVedtak": 42803157,
    "etterbetaling": null,
    "utvidetSamordningsfrist": false,
    "vedtakLast": true,
    "behandlingType": "AUTO",
    "annenPengemot": null,
    "virkGammeltVedtak": null,
    "innstillingsverdi": null,
    "klageAnkeResultat": null,
    "relatertAfpPrivatVedtak": null,
    "relatertAlderVedtak": null,
    "hovedkravlinjeResultat": null,
    "simulertFeilutbetalingTom": null,
    "reguleringSkalFullberegnes": false,
    "videreforTilUt": null,
    "automatiskOmregningAvUT": null,
    "beregnetUtbetalingsinfoUT": 24297897,
    "etteroppgjorResultat": null,
    "varslingStatus": null,
    "sendtNavi": null,
    "tilbakekrevingTotalResultat": null,
    "merknaderPen": [],
    "beregnetUforehistorikkId": 61918905,
    "version": 5,
    "changeStamp": {
        "createdDate": "2023-06-13T10:04:08+0200",
        "createdBy": "BPEN068",
        "updatedDate": "2023-06-13T11:26:14+0200",
        "updatedBy": "BPEN069"
    }
}
]"""
}
