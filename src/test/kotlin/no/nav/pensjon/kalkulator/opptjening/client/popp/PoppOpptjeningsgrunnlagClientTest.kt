package no.nav.pensjon.kalkulator.opptjening.client.popp

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
class PoppOpptjeningsgrunnlagClientTest : WebClientTest() {

    private lateinit var client: PoppOpptjeningsgrunnlagClient

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = PoppOpptjeningsgrunnlagClient(baseUrl(), WebClientConfig().regularWebClient(), traceAid, RETRY_ATTEMPTS)
    }

    @Test
    fun `fetchOpptjeningsgrunnlag returns opptjeningsgrunnlag when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response: Opptjeningsgrunnlag = client.fetchOpptjeningsgrunnlag(pid)

        val inntekt2017 = response.inntekter.first { it.aar == 2017 }
        val inntekt2018 = response.inntekter.first { it.aar == 2018 }
        assertEquals(BigDecimal("280241"), inntekt2017.beloep)
        assertEquals(Opptjeningstype.OTHER, inntekt2017.type)
        assertEquals(BigDecimal("280242"), inntekt2018.beloep)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt2018.type)
    }

    @Test
    fun `fetchOpptjeningsgrunnlag retries in case of server error`() {
        arrangeSecurityContext()
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okResponse())

        val response: Opptjeningsgrunnlag = client.fetchOpptjeningsgrunnlag(pid)

        val inntekt2017 = response.inntekter.first { it.aar == 2017 }
        val inntekt2018 = response.inntekter.first { it.aar == 2018 }
        assertEquals(BigDecimal("280241"), inntekt2017.beloep)
        assertEquals(Opptjeningstype.OTHER, inntekt2017.type)
        assertEquals(BigDecimal("280242"), inntekt2018.beloep)
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt2018.type)
    }

    @Test
    fun `fetchOpptjeningsgrunnlag does not retry in case of client error`() {
        arrangeSecurityContext()
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        val exception = assertThrows(EgressException::class.java) { client.fetchOpptjeningsgrunnlag(pid) }

        assertEquals("My bad", exception.message)
    }

    @Test
    fun `fetchOpptjeningsgrunnlag handles server error`() {
        arrangeSecurityContext()
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil")) // for retry

        val exception = assertThrows(EgressException::class.java) { client.fetchOpptjeningsgrunnlag(pid) }

        assertEquals("Failed calling ${baseUrl()}/popp/api/opptjeningsgrunnlag", exception.message)
        assertEquals("Feil", (exception.cause as EgressException).message)
    }

    companion object {
        private const val RETRY_ATTEMPTS = "1"

        @Language("json")
        private const val RESPONSE_BODY =
            """
             {
                 "opptjeningsGrunnlag": {
                     "fnr": "04925398980",
                     "inntektListe": [
                         {
                             "changeStamp": {
                                 "createdBy": "TESTDATA",
                                 "createdDate": 1586931866460,
                                 "updatedBy": "srvpensjon",
                                 "updatedDate": 1586931866775
                             },
                             "inntektId": 585473583,
                             "fnr": "04925398980",
                             "inntektAr": 2017,
                             "kilde": "PEN",
                             "kommune": "1337",
                             "piMerke": null,
                             "inntektType": "INN_LON",
                             "belop": 280241
                         },
                         {
                             "changeStamp": {
                                 "createdBy": "srvpensjon",
                                 "createdDate": 1586931866782,
                                 "updatedBy": "srvpensjon",
                                 "updatedDate": 1586931866946
                             },
                             "inntektId": 585473584,
                             "fnr": "04925398980",
                             "inntektAr": 2018,
                             "kilde": "POPP",
                             "kommune": null,
                             "piMerke": null,
                             "inntektType": "SUM_PI",
                             "belop": 280242
                         }
                     ],
                     "omsorgListe": [],
                     "dagpengerListe": [],
                     "forstegangstjeneste": null
                 }
             }
             """

        private fun okResponse() = jsonResponse().setBody(RESPONSE_BODY.trimIndent())
    }
}
