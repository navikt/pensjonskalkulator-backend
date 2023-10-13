package no.nav.pensjon.kalkulator.grunnbeloep.client.regler

import no.nav.pensjon.kalkulator.grunnbeloep.Grunnbeloep
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepSpec
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.regler.ReglerConfiguration
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class PensjonReglerGrunnbeloepClientTest : WebClientTest() {

    private lateinit var client: PensjonReglerGrunnbeloepClient

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = PensjonReglerGrunnbeloepClient(
            baseUrl = baseUrl(),
            webClient = WebClientConfig().regularWebClient(),
            objectMapper = ReglerConfiguration().objectMapper(),
            traceAid = traceAid,
            retryAttempts = "1"
        )

        arrangeSecurityContext()
    }

    @Test
    fun `getGrunnbeloep returns grunnbeloep when OK response`() {
        arrange(okResponse())
        val spec = GrunnbeloepSpec(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2))

        val response: Grunnbeloep = client.getGrunnbeloep(spec)

        ByteArrayOutputStream().use {
            takeRequest().body.copyTo(it)
            assertEquals(
                """{"fom":1672567200000,"tom":1672653600000}""",
                it.toString(StandardCharsets.UTF_8)
            )
        }

        assertEquals(111477, response.value)
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
