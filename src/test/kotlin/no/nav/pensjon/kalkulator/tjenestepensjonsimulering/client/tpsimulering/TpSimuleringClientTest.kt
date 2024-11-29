package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class TpSimuleringClientTest : WebClientTest() {

    private lateinit var client: TpSimuleringClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = TpSimuleringClient(
            baseUrl = baseUrl(),
            webClientBuilder = webClientBuilder,
            traceAid = traceAid,
            retryAttempts = RETRY_ATTEMPTS
        )

        arrangeSecurityContext()
    }

    @Test
    fun `hent tjenestepensjonSimulering hvor responsen har ingen utbetalingsperioder`() {
        arrange(ingenUtbetalingsperioderResponse())
        val req = SimuleringOFTPSpec(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 0,
            aarIUtlandetEtter16 = 1,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
        )
        val resp = client.hentTjenestepensjonSimulering(req, pid)

        assertNull(resp.simuleringsResultat)
        assertEquals(ResultatType.TOM_RESPONS, resp.simuleringsResultatStatus.resultatType)
        assertEquals("Simulering fra Statens Pensjonskasse inneholder ingen utbetalingsperioder", resp.simuleringsResultatStatus.feilmelding)
        assertEquals(listOf("Statens pensjonskasse"), resp.tpOrdninger)
    }

    @Test
    fun `hent tjenestepensjon simulering OK`() {
        arrange(okResponse())
        val req = SimuleringOFTPSpec(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 500000,
            aarIUtlandetEtter16 = 1,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
        )
        val resp = client.hentTjenestepensjonSimulering(req, pid)

        assertNotNull(resp.simuleringsResultat)
        assertEquals(ResultatType.OK, resp.simuleringsResultatStatus.resultatType)
        assertNull(resp.simuleringsResultatStatus.feilmelding)
        assertEquals(listOf("Statens pensjonskasse"), resp.tpOrdninger)
        assertEquals(23, resp.simuleringsResultat!!.perioder.size)
        assertEquals(63, resp.simuleringsResultat.perioder[0].aar)
        assertEquals(57225, resp.simuleringsResultat.perioder[0].beloep)
    }

    @Test
    fun `hent tjenestepensjon simulering for bruker som ikke er medlem`() {
        arrange(ikkeMedlemResponse())
        val req = SimuleringOFTPSpec(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 500000,
            aarIUtlandetEtter16 = 1,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
        )
        val resp = client.hentTjenestepensjonSimulering(req, pid)

        assertNull(resp.simuleringsResultat)
        assertEquals(ResultatType.IKKE_MEDLEM, resp.simuleringsResultatStatus.resultatType)
        assertNotNull(resp.simuleringsResultatStatus.feilmelding)
        assertTrue(resp.tpOrdninger.isEmpty())
    }

    @Test
    fun `hent tjenestepensjon simulering for tp-ordning som ikke stoettes`() {
        arrange(tpOrdningStoettesIkkeResponse())
        val req = SimuleringOFTPSpec(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 500000,
            aarIUtlandetEtter16 = 1,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
        )
        val resp = client.hentTjenestepensjonSimulering(req, pid)

        assertNull(resp.simuleringsResultat)
        assertEquals(ResultatType.TP_ORDNING_STOETTES_IKKE, resp.simuleringsResultatStatus.resultatType)
        assertNotNull(resp.simuleringsResultatStatus.feilmelding)
        assertTrue(resp.tpOrdninger.isNotEmpty())
        assertEquals("Pensjonskasse A", resp.tpOrdninger[0])
    }

    @Test
    fun `hent tjenestepensjon simulering kom med teksnisk feil fra tp-ordning`() {
        arrange(tekniskFeilResponse())
        val req = SimuleringOFTPSpec(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 500000,
            aarIUtlandetEtter16 = 1,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
        )
        val resp = client.hentTjenestepensjonSimulering(req, pid)

        assertNull(resp.simuleringsResultat)
        assertEquals(ResultatType.TEKNISK_FEIL, resp.simuleringsResultatStatus.resultatType)
        assertNotNull(resp.simuleringsResultatStatus.feilmelding)
        assertTrue(resp.tpOrdninger.isNotEmpty())
        assertEquals("Pensjonskasse A", resp.tpOrdninger[0])
    }

    companion object {
        private const val RETRY_ATTEMPTS = "1"
        private fun ingenUtbetalingsperioderResponse() = jsonResponse().setBody(ingenUtbetalingsperioder().trimIndent())
        private fun ikkeMedlemResponse() = jsonResponse().setBody(ikkeMedlem().trimIndent())
        private fun tpOrdningStoettesIkkeResponse() = jsonResponse().setBody(tpOrdningStoettesIkke().trimIndent())
        private fun tekniskFeilResponse() = jsonResponse().setBody(tekniskFeil().trimIndent())
        private fun okResponse() = jsonResponse().setBody(ok().trimIndent())
        @Language("json")
        private fun ingenUtbetalingsperioder() = """{
  "simuleringsResultatStatus": {
    "resultatType": "INGEN_UTBETALINGSPERIODER_FRA_TP_ORDNING",
    "feilmelding": "Simulering fra Statens Pensjonskasse inneholder ingen utbetalingsperioder"
  },
  "simuleringsResultat": null,
  "relevanteTpOrdninger": [
    "Statens pensjonskasse"
  ]
}"""
        @Language("json")
        private fun ikkeMedlem() = """{
  "simuleringsResultatStatus": {
    "resultatType": "BRUKER_ER_IKKE_MEDLEM_HOS_TP_ORDNING",
    "feilmelding": "Ikke medlem"
  },
  "simuleringsResultat": null,
  "relevanteTpOrdninger": []
}"""
        @Language("json")
        private fun tpOrdningStoettesIkke() = """{
  "simuleringsResultatStatus": {
    "resultatType": "TP_ORDNING_ER_IKKE_STOTTET",
    "feilmelding": "Stoettes ikke"
  },
  "simuleringsResultat": null,
  "relevanteTpOrdninger": ["Pensjonskasse A"]
}"""
        @Language("json")
        private fun tekniskFeil() = """{
  "simuleringsResultatStatus": {
    "resultatType": "TEKNISK_FEIL_FRA_TP_ORDNING",
    "feilmelding": "Bruker er bare 2 år gammel"
  },
  "simuleringsResultat": null,
  "relevanteTpOrdninger": ["Pensjonskasse A"]
}"""

        @Language("json")
        private fun ok() = """{
  "simuleringsResultatStatus": {
    "resultatType": "SUCCESS",
    "feilmelding": null
  },
  "simuleringsResultat": {
    "tpLeverandoer": "Statens Pensjonskasse",
    "utbetalingsperioder": [
      {
        "aar": 63,
        "beloep": 57225
      },
      {
        "aar": 64,
        "beloep": 228900
      },
      {
        "aar": 65,
        "beloep": 228900
      },
      {
        "aar": 66,
        "beloep": 228900
      },
      {
        "aar": 67,
        "beloep": 209112
      },
      {
        "aar": 68,
        "beloep": 209112
      },
      {
        "aar": 69,
        "beloep": 209112
      },
      {
        "aar": 70,
        "beloep": 209112
      },
      {
        "aar": 71,
        "beloep": 209112
      },
      {
        "aar": 72,
        "beloep": 209112
      },
      {
        "aar": 73,
        "beloep": 209112
      },
      {
        "aar": 74,
        "beloep": 209112
      },
      {
        "aar": 75,
        "beloep": 209112
      },
      {
        "aar": 76,
        "beloep": 209112
      },
      {
        "aar": 77,
        "beloep": 209112
      },
      {
        "aar": 78,
        "beloep": 209112
      },
      {
        "aar": 79,
        "beloep": 209112
      },
      {
        "aar": 80,
        "beloep": 209112
      },
      {
        "aar": 81,
        "beloep": 209112
      },
      {
        "aar": 82,
        "beloep": 209112
      },
      {
        "aar": 83,
        "beloep": 209112
      },
      {
        "aar": 84,
        "beloep": 209112
      },
      {
        "aar": 85,
        "beloep": 209112
      }
    ],
    "betingetTjenestepensjonErInkludert": false
  },
  "relevanteTpOrdninger": [
    "Statens pensjonskasse"
  ]
}"""
    }
}