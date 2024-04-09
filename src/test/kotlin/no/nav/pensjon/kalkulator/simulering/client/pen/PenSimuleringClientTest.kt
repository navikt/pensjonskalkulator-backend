package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Assertions.assertAlder
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PenSimuleringClientTest : WebClientTest() {

    private lateinit var client: PenSimuleringClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = PenSimuleringClient(baseUrl(), webClientBuilder, traceAid, retryAttempts = "1")
        arrangeSecurityContext()
    }

    /**
     * NAU = Nærmest Angitt Uttak (alternative simuleringsparametre)
     */
    @Test
    fun `simulerAlderspensjon der responsen har alternativ NAU-beregning`() {
        arrange(alternativPensjonResponse())

        val response: Simuleringsresultat = client.simulerAlderspensjon(impersonalSpec(), personalSpec())

        with(response.alderspensjon) {
            assertEquals(15, size)
            assertPensjon(expectedAlderAar = 63, expectedBeloep = 13956, actualPensjon = this[0])
            assertPensjon(expectedAlderAar = 77, expectedBeloep = 222612, actualPensjon = this[14])
        }
        assertTrue(response.afpPrivat.isEmpty())
        with(response.vilkaarsproeving) {
            assertFalse(innvilget)
            with(alternativ!!) {
                assertEquals(Uttaksgrad.FOERTI_PROSENT, uttakGrad)
                assertAlder(expectedAar = 63, expectedMaaneder = 10, actualAlder = gradertUttakAlder!!)
                assertAlder(expectedAar = 65, expectedMaaneder = 6, actualAlder = heltUttakAlder)
            }
        }
    }

    @Test
    fun `simulerAlderspensjon sends request body with gradert uttak when specified`() {
        arrange(alternativPensjonResponse())
        client.simulerAlderspensjon(impersonalGradertUttakSpec(), personalSpec())
        assertGradertUttakRequestBody()
    }

    private fun assertGradertUttakRequestBody() {
        ByteArrayOutputStream().use {
            takeRequest().body.copyTo(it)
            assertEquals(EXPECTED_GRADERT_UTTAK_REQUEST_BODY, it.toString(StandardCharsets.UTF_8))
        }
    }

    private companion object {

        @Language("json")
        private const val EXPECTED_GRADERT_UTTAK_REQUEST_BODY = """{
  "simuleringstype" : "ALDER",
  "pid" : "12906498357",
  "sivilstand" : "ENKE",
  "harEps" : true,
  "sisteInntekt" : 123000,
  "uttaksar" : 1,
  "gradertUttak" : {
    "grad" : "P_50",
    "uttakFomAlder" : {
      "aar" : 64,
      "maaneder" : 2
    },
    "aarligInntekt" : 12000
  },
  "heltUttak" : {
    "uttakFomAlder" : {
      "aar" : 67,
      "maaneder" : 1
    },
    "aarligInntekt" : 0,
    "inntektTomAlder" : {
      "aar" : 67,
      "maaneder" : 1
    }
  }
}"""

        /**
         * Actual response from pensjon-pen-q2 2024-03-19
         */
        @Language("json")
        private const val ALTERNATIV_PENSJON = """{
    "alderspensjon": [
        {
            "alder": 63,
            "beloep": 13956
        },
        {
            "alder": 64,
            "beloep": 83736
        },
        {
            "alder": 65,
            "beloep": 153174
        },
        {
            "alder": 66,
            "beloep": 222612
        },
        {
            "alder": 67,
            "beloep": 222612
        },
        {
            "alder": 68,
            "beloep": 222612
        },
        {
            "alder": 69,
            "beloep": 222612
        },
        {
            "alder": 70,
            "beloep": 222612
        },
        {
            "alder": 71,
            "beloep": 222612
        },
        {
            "alder": 72,
            "beloep": 222612
        },
        {
            "alder": 73,
            "beloep": 222612
        },
        {
            "alder": 74,
            "beloep": 222612
        },
        {
            "alder": 75,
            "beloep": 222612
        },
        {
            "alder": 76,
            "beloep": 222612
        },
        {
            "alder": 77,
            "beloep": 222612
        }
    ],
    "afpPrivat": [],
    "afpOffentliglivsvarig": [],
    "vilkaarsproeving": {
        "vilkaarErOppfylt": false,
        "alternativ": {
            "gradertUttaksalder": {
                "aar": 63,
                "maaneder": 10
            },
            "uttaksgrad": 40,
            "heltUttaksalder": {
                "aar": 65,
                "maaneder": 6
            }
        }
    }
}"""

        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = null,
                epsHarInntektOver2G = true,
                forventetAarligInntektFoerUttak = null,
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(aar = 67, maaneder = 1),
                    inntekt = null
                )
            )

        private fun impersonalGradertUttakSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = null,
                epsHarInntektOver2G = true,
                forventetAarligInntektFoerUttak = null,
                gradertUttak = GradertUttak(
                    grad = Uttaksgrad.FEMTI_PROSENT,
                    uttakFomAlder = Alder(aar = 64, maaneder = 2),
                    aarligInntekt = 12_000
                ),
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(aar = 67, maaneder = 1),
                    inntekt = null
                )
            )

        private fun personalSpec() =
            PersonalSimuleringSpec(
                pid = pid,
                aarligInntektFoerUttak = 123000,
                sivilstand = Sivilstand.ENKE_ELLER_ENKEMANN
            )

        private fun alternativPensjonResponse() = jsonResponse(HttpStatus.OK).setBody(ALTERNATIV_PENSJON)

        private fun assertPensjon(expectedAlderAar: Int, expectedBeloep: Int, actualPensjon: SimulertAlderspensjon) {
            with(actualPensjon) {
                assertEquals(expectedAlderAar, alder)
                assertEquals(expectedBeloep, beloep)
            }
        }
    }
}
