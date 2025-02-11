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
        val req = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 0,
            fremtidigeInntekter = emptyList(),
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
        val req = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 500000,
            fremtidigeInntekter = listOf(
                FremtidigInntektV2(LocalDate.of(2028, 1, 1), 600000),
                FremtidigInntektV2(LocalDate.of(2029, 1, 1), 700000),
                FremtidigInntektV2(LocalDate.of(2030, 1, 1), 0),
            ),
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
        assertEquals("Statens Pensjonskasse", resp.simuleringsResultat!!.tpOrdning)
        assertEquals("1", resp.simuleringsResultat!!.tpNummer)
        assertEquals(3, resp.simuleringsResultat!!.perioder.size)
        assertEquals(62, resp.simuleringsResultat!!.perioder[0].startAlder.aar)
        assertEquals(1, resp.simuleringsResultat!!.perioder[0].startAlder.maaneder)
        assertEquals(63, resp.simuleringsResultat!!.perioder[0].sluttAlder?.aar)
        assertEquals(3, resp.simuleringsResultat!!.perioder[0].sluttAlder?.maaneder)
        assertEquals(1000, resp.simuleringsResultat!!.perioder[0].maanedligBeloep)

        assertEquals(63, resp.simuleringsResultat!!.perioder[1].startAlder.aar)
        assertEquals(4, resp.simuleringsResultat!!.perioder[1].startAlder.maaneder)
        assertEquals(63, resp.simuleringsResultat!!.perioder[1].sluttAlder?.aar)
        assertEquals(6, resp.simuleringsResultat!!.perioder[1].sluttAlder?.maaneder)
        assertEquals(2000, resp.simuleringsResultat!!.perioder[1].maanedligBeloep)

        assertEquals(63, resp.simuleringsResultat!!.perioder[2].startAlder.aar)
        assertEquals(7, resp.simuleringsResultat!!.perioder[2].startAlder.maaneder)
        assertNull(resp.simuleringsResultat!!.perioder[2].sluttAlder)
        assertEquals(3000, resp.simuleringsResultat!!.perioder[2].maanedligBeloep)
    }

    @Test
    fun `hent tjenestepensjon simulering for bruker som ikke er medlem`() {
        arrange(ikkeMedlemResponse())
        val req = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = listOf(FremtidigInntektV2(LocalDate.of(2027, 2, 1), 0),),
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
        val req = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 500000,
            fremtidigeInntekter = listOf(FremtidigInntektV2(LocalDate.of(2027, 2, 1), 0),),
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
        val req = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1964, 2, 3),
            uttaksdato = LocalDate.of(2027, 2, 3),
            sisteInntekt = 500000,
            fremtidigeInntekter = listOf(FremtidigInntektV2(LocalDate.of(2027, 2, 1), 0),),
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
    "tpNummer": "1",
    "utbetalingsperioder": [
    {
        "startAlder": {
            "aar": 62,
            "maaneder": 1
        },
        "sluttAlder": {
            "aar": 63,
            "maaneder": 3
        },
        "maanedligBeloep": 1000
    },
    {
        "startAlder": {
            "aar": 63,
            "maaneder": 4
        },
        "sluttAlder": {
            "aar": 63,
            "maaneder": 6
        },
        "maanedligBeloep": 2000
    },
    {
        "startAlder": {
            "aar": 63,
            "maaneder": 7
        },
        "sluttAlder": null,
        "maanedligBeloep": 3000
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