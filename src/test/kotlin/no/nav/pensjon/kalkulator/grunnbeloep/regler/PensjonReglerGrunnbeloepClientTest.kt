package no.nav.pensjon.kalkulator.grunnbeloep.regler

import no.nav.pensjon.kalkulator.grunnbeloep.regler.dto.SatsResponse
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.regler.ReglerConfiguration
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class PensjonReglerGrunnbeloepClientTest : WebClientTest() {

    private lateinit var client: PensjonReglerGrunnbeloepClient

    @BeforeEach
    fun initialize() {
        client = PensjonReglerGrunnbeloepClient(baseUrl(), WebClient.create(), ReglerConfiguration().objectMapper())
    }

    @Test
    fun `getGrunnbeloep returns satsresultater when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response: SatsResponse = client.getGrunnbeloep("")

        val resultat = response.satsResultater?.get(0)!!
        assertEquals(LocalDate.of(2022, 5, 1), resultat.fom)
        assertEquals(LocalDate.of(9999, 12, 31), resultat.tom)
        assertEquals(111477.0, resultat.verdi)
    }

    companion object {

        private fun arrangeSecurityContext() {
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

            SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
                TestingAuthenticationToken("TEST_USER", null),
                EgressTokenSuppliersByService(mapOf())
            )
        }

        private fun okResponse(): MockResponse {
            // Actual response from pensjon-regler in Q2:
            return jsonResponse(HttpStatus.OK)
                .setBody(
                    """{
    "satsResultater": ["java.util.ArrayList", [{
        "fom": 1651399200000,
        "tom": 253402254000000,
        "verdi": 111477.0
    }]]
}""".trimIndent()
                )
        }
    }
}
