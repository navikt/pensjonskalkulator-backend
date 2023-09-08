package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class PenSimuleringClientTest : WebClientTest() {

    private lateinit var client: PenSimuleringClient

    @Mock
    private lateinit var callIdGenerator: CallIdGenerator

    @BeforeEach
    fun initialize() {
        client = PenSimuleringClient(baseUrl(), WebClientConfig().regularWebClient(), callIdGenerator, "1")
        arrangeSecurityContext()
    }

    @Test
    fun `simulerAlderspensjon handles single pensjonsavtale`() {
        arrange(okResponse())

        val response = client.simulerAlderspensjon(spec())

        with(response.alderspensjon[0]) {
            assertEquals(65, alder)
            assertEquals(98000, beloep)
        }
    }

    private companion object {

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

        private fun spec() =
            SimuleringSpec(
                simuleringstype = SimuleringType.ALDERSPENSJON,
                pid = pid,
                forventetInntekt = 123000,
                uttaksgrad = 80,
                foersteUttaksdato = LocalDate.of(2034, 5, 6),
                sivilstand = Sivilstand.ENKE_ELLER_ENKEMANN,
                epsHarInntektOver2G = true
            )

        private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(PENSJON)
    }
}
