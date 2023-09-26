package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
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
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = PenSimuleringClient(baseUrl(), WebClientConfig().regularWebClient(), traceAid, "1")
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

        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                uttaksgrad = Uttaksgrad.AATTI_PROSENT,
                foersteUttaksalder = Alder(67, 1),
                foedselsdato = LocalDate.of(1963, 1, 1),
                epsHarInntektOver2G = true,
                forventetInntekt = null,
                sivilstand = null
            )

        private fun personalSpec() =
            PersonalSimuleringSpec(
                pid = pid,
                forventetInntekt = 123000,
                sivilstand = Sivilstand.ENKE_ELLER_ENKEMANN
            )

        private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(PENSJON)
    }
}
