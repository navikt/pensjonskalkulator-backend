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
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
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
import java.time.LocalDate

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
        client = PenSimuleringClient(baseUrl(), webClientBuilder, traceAid, "1")
        arrangeSecurityContext()
    }

    @Test
    fun `simulerAlderspensjon handles single pensjonsavtale`() {
        arrange(okResponse())

        val response = client.simulerAlderspensjon(impersonalSpec(), personalSpec())

        with(response.alderspensjon[0]) {
            assertEquals(65, alder)
            assertEquals(98000, beloep)
        }
    }

    @Test
    fun `simulerAlderspensjon sends request body with gradert uttak when specified`() {
        arrange(okResponse())

        val response = client.simulerAlderspensjon(impersonalGradertUttakSpec(), personalSpec())

        assertGradertUttakRequestBody()
        with(response.alderspensjon[0]) {
            assertEquals(65, alder)
            assertEquals(98000, beloep)
        }
    }

    private fun assertGradertUttakRequestBody() {
        ByteArrayOutputStream().use {
            val request = takeRequest()
            request.body.copyTo(it)
            assertEquals(EXPECTED_GRADERT_UTTAK_REQUEST_BODY, it.toString(StandardCharsets.UTF_8))
        }
    }

    private companion object {

        // forsteUttaksdato: 1963/1 + 64/2 + 0/1 = 2027/4 => 2027-04-01 00:00:00 UTC+2 => epoch 1806530400000
        // heltUttakDato:    1963/1 + 67/1 + 0/1 = 2030/3 => 2030-03-01 00:00:00 UTC+2 => epoch 1898550000000
        @Language("json")
        private const val EXPECTED_GRADERT_UTTAK_REQUEST_BODY = """{
  "simuleringstype" : "ALDER",
  "pid" : "12906498357",
  "sivilstand" : "ENKE",
  "harEps" : true,
  "sisteInntekt" : 123000,
  "uttaksar" : 1,
  "forsteUttaksdato" : 1806530400000,
  "uttaksgrad" : "P_50",
  "inntektUnderGradertUttak" : 12000,
  "heltUttakDato" : 1898550000000
}"""

        @Language("json")
        private const val PENSJON = """{
              "alderspensjon": [
                {
                  "alder": "65",
                  "beloep": "98000"
                }
              ],
              "afpPrivat": []
            }"""

        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = null,
                epsHarInntektOver2G = true,
                forventetAarligInntektFoerUttak = null,
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(67, 1),
                    inntekt = null,
                    foedselDato = LocalDate.of(1963, 1, 1)
                )
            )

        private fun impersonalGradertUttakSpec(): ImpersonalSimuleringSpec {
            val foedselDato = LocalDate.of(1963, 1, 1)

            return ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = null,
                epsHarInntektOver2G = true,
                forventetAarligInntektFoerUttak = null,
                gradertUttak = GradertUttak(
                    grad = Uttaksgrad.FEMTI_PROSENT,
                    uttakFomAlder = Alder(64, 2),
                    aarligInntekt = 12_000,
                    foedselDato = foedselDato
                ),
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(67, 1),
                    inntekt = null,
                    foedselDato = foedselDato
                )
            )
        }

        private fun personalSpec() =
            PersonalSimuleringSpec(
                pid = pid,
                forventetInntekt = 123000,
                sivilstand = Sivilstand.ENKE_ELLER_ENKEMANN
            )

        private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(PENSJON)
    }
}
