package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class PenSimuleringClientTest : WebClientTest() {

    private lateinit var client: PenSimuleringClient

    @BeforeEach
    fun initialize() {
        client = PenSimuleringClient(baseUrl(), WebClient.create())
    }

    @Test
    fun `simulerAlderspensjon handles single pensjonsavtale`() {
        arrangeSecurityContext()
        arrange(okResponse(pensjon()))

        val response = client.simulerAlderspensjon(simuleringSpec())

        assertEquals(65, response.alderspensjon[0].alder)
        assertEquals(98000, response.alderspensjon[0].belop)
    }

    companion object {

        private fun arrangeSecurityContext() {
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

            SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
                TestingAuthenticationToken("TEST_USER", null),
                EgressTokenSuppliersByService(mapOf())
            )
        }

        private fun simuleringSpec() = SimuleringSpec(
            " simuleringstype1",
            Pid("12906498357"),
            123000,
            80,
            LocalDate.of(2034, 5, 6),
            Sivilstand.ENKE_ELLER_ENKEMANN,
            true
        )

        private fun okResponse(pensjon: String) =
            jsonResponse(HttpStatus.OK).setBody(pensjon)

        @Language("json")
        private fun pensjon() = """{
              "alderspensjon": [
                {
                  "alder": "65",
                  "belop": "98000"
                }
              ],
              "afpPrivat": []
            }"""
    }
}
