package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.ResultatType
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.SimuleringOffentligTjenestepensjonSpec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.SimuleringsResultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.SimuleringsResultatStatus
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.Utbetaling
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.TpSimuleringClientTestObjects.IKKE_MEDLEM
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.TpSimuleringClientTestObjects.INGEN_UTBETALINGSPERIODER
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.TpSimuleringClientTestObjects.OK
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.TpSimuleringClientTestObjects.TEKNISK_FEIL
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.TpSimuleringClientTestObjects.TP_ORDNING_STOETTES_IKKE
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.TpSimuleringClientTestObjects.spec
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class TpSimuleringClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        TpSimuleringClient(
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

    test("hent tjenestepensjonssimulering hvor responsen har ingen utbetalingsperioder") {
        server?.arrangeOkJsonResponse(INGEN_UTBETALINGSPERIODER)

        Arrange.webClientContextRunner().run {
            val result = client(context = it).hentTjenestepensjonSimulering(spec, pid)

            result shouldBe OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(
                    resultatType = ResultatType.TOM_RESPONS,
                    feilmelding = "Simulering fra Statens Pensjonskasse inneholder ingen utbetalingsperioder"
                ),
                simuleringsResultat = null,
                tpOrdninger = listOf("Statens pensjonskasse"),
                serviceData = emptyList()
            )
        }
    }

    test("hent tjenestepensjonssimulering OK") {
        server?.arrangeOkJsonResponse(OK)

        Arrange.webClientContextRunner().run {
            val result = client(context = it).hentTjenestepensjonSimulering(spec, pid)

            result shouldBe OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(
                    resultatType = ResultatType.OK,
                    feilmelding = null
                ),
                simuleringsResultat = SimuleringsResultat(
                    tpOrdning = "Statens Pensjonskasse",
                    tpNummer = "1",
                    perioder = listOf(
                        Utbetaling(
                            startAlder = Alder(62, 1),
                            sluttAlder = Alder(63, 3),
                            maanedligBeloep = 1000
                        ),
                        Utbetaling(
                            startAlder = Alder(63, 4),
                            sluttAlder = Alder(63, 6),
                            maanedligBeloep = 2000
                        ),
                        Utbetaling(
                            startAlder = Alder(63, 7),
                            sluttAlder = null,
                            maanedligBeloep = 3000
                        )
                    ),
                    betingetTjenestepensjonInkludert = false
                ),
                tpOrdninger = listOf("Statens pensjonskasse"),
                serviceData = emptyList()
            )
        }
    }

    test("hent tjenestepensjonssimulering for person som ikke er medlem") {
        server?.arrangeOkJsonResponse(IKKE_MEDLEM)

        Arrange.webClientContextRunner().run {
            val result = client(context = it).hentTjenestepensjonSimulering(spec, pid)

            result shouldBe OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(
                    resultatType = ResultatType.IKKE_MEDLEM,
                    feilmelding = "Ikke medlem"
                ),
                simuleringsResultat = null,
                tpOrdninger = emptyList(),
                serviceData = emptyList()
            )
        }
    }

    test("hent tjenestepensjonssimulering for TP-ordning som ikke stoettes") {
        server?.arrangeOkJsonResponse(TP_ORDNING_STOETTES_IKKE)

        Arrange.webClientContextRunner().run {
            val result = client(context = it).hentTjenestepensjonSimulering(spec, pid)

            result shouldBe OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(
                    resultatType = ResultatType.TP_ORDNING_STOETTES_IKKE,
                    feilmelding = "Stoettes ikke"
                ),
                simuleringsResultat = null,
                tpOrdninger = listOf("Pensjonskasse A"),
                serviceData = emptyList()
            )
        }
    }

    test("hent tjenestepensjonssimulering kom med teknisk feil fra TP-ordning") {
        server?.arrangeOkJsonResponse(TEKNISK_FEIL)

        Arrange.webClientContextRunner().run {
            val result = client(context = it).hentTjenestepensjonSimulering(spec, pid)

            result shouldBe OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(
                    resultatType = ResultatType.TEKNISK_FEIL,
                    feilmelding = "Bruker er bare 2 år gammel"
                ),
                simuleringsResultat = null,
                tpOrdninger = listOf("Pensjonskasse A"),
                serviceData = emptyList()
            )
        }
    }
})

object TpSimuleringClientTestObjects {

    @Language("json")
    const val INGEN_UTBETALINGSPERIODER = """{
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
    const val IKKE_MEDLEM = """{
  "simuleringsResultatStatus": {
    "resultatType": "BRUKER_ER_IKKE_MEDLEM_HOS_TP_ORDNING",
    "feilmelding": "Ikke medlem"
  },
  "simuleringsResultat": null,
  "relevanteTpOrdninger": []
}"""

    @Language("json")
    const val TP_ORDNING_STOETTES_IKKE = """{
  "simuleringsResultatStatus": {
    "resultatType": "TP_ORDNING_ER_IKKE_STOTTET",
    "feilmelding": "Stoettes ikke"
  },
  "simuleringsResultat": null,
  "relevanteTpOrdninger": ["Pensjonskasse A"]
}"""

    @Language("json")
    const val TEKNISK_FEIL = """{
  "simuleringsResultatStatus": {
    "resultatType": "TEKNISK_FEIL_FRA_TP_ORDNING",
    "feilmelding": "Bruker er bare 2 år gammel"
  },
  "simuleringsResultat": null,
  "relevanteTpOrdninger": ["Pensjonskasse A"]
}"""

    @Language("json")
    const val OK = """{
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

    val spec = SimuleringOffentligTjenestepensjonSpec(
        foedselsdato = LocalDate.of(1964, 6, 15),
        uttaksdato = LocalDate.of(2027, 2, 1),
        sisteInntekt = 0,
        fremtidigeInntekter = emptyList(),
        aarIUtlandetEtter16 = 1,
        brukerBaOmAfp = true,
        epsPensjon = false,
        eps2G = true,
        erApoteker = false
    )
}
