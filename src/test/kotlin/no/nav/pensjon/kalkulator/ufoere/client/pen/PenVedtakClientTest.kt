package no.nav.pensjon.kalkulator.ufoere.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.DateFactory.date
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.ufoere.Sakstype
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PenVedtakClientTest : WebClientTest() {

    private lateinit var client: PenVedtakClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        `when`(traceAid.callId()).thenReturn("id1")
        client = PenVedtakClient(baseUrl(), webClientBuilder, traceAid, "1")
        arrangeSecurityContext()
    }

    @Test
    fun `bestemGjeldendeVedtak uses supplied PID in request and returns sakstype`() {
        arrange(okResponse())

        val vedtaksliste = client.bestemGjeldendeVedtak(pid, date)

        with(takeRequest()) {
            getHeader("fnr") shouldBe pid.value
            requestUrl?.queryParameter("fom") shouldBe "2023-04-05"
        }
        vedtaksliste[0].sakstype shouldBe Sakstype.UFOEREPENSJON
    }

    @Test
    fun `bestemGjeldendeVedtak retries in case of server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okResponse())

        val response = client.bestemGjeldendeVedtak(pid, date)

        response[0].sakstype shouldBe Sakstype.UFOEREPENSJON
    }

    @Test
    fun `bestemGjeldendeVedtak does not retry in case of client error`() {
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        shouldThrow<EgressException> { client.bestemGjeldendeVedtak(pid, date) }.message shouldBe "My bad"
    }

    @Test
    fun `bestemGjeldendeVedtak handles server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil")) // for retry

        val exception = shouldThrow<EgressException> { client.bestemGjeldendeVedtak(pid, date) }

        with(exception) {
            message shouldBe "Failed calling /api/vedtak/bestemgjeldende?fom=2023-04-05"
            (cause as EgressException).message shouldBe "Feil"
        }
    }

    private companion object {
        private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(RESPONSE)

        @Language("JSON")
        private const val RESPONSE = """[
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
}
