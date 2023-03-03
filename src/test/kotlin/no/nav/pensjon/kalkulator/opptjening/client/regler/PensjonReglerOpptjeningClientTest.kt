package no.nav.pensjon.kalkulator.opptjening.client.regler

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.opptjening.Opptjeningshistorikk
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningSpec
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningshistorikkSpec
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
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class PensjonReglerOpptjeningClientTest : WebClientTest() {

    private lateinit var client: PensjonReglerOpptjeningClient

    @BeforeEach
    fun initialize() {
        client =
            PensjonReglerOpptjeningClient(
                baseUrl(),
                WebClient.create(),
                ReglerConfiguration().objectMapper()
            )
    }

    @Test
    fun `getOpptjening returns opptjening when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response: Opptjeningshistorikk = client.getOpptjeningshistorikk(opptjeningshistorikkSpec())

        ByteArrayOutputStream().use {
            takeRequest().body.copyTo(it)
            assertEquals(
                """{"personOpptjeningsgrunnlagListe":[{"opptjening":{"ar":2022,"pi":1000000,"opptjeningType":{"kode":"PPI"}},"fodselsdato":-220888800000}]}""",
                it.toString(StandardCharsets.UTF_8)
            )
        }

        val opptjening = response.opptjeningPerAar[2022]!!
        assertEquals(772469, opptjening.anvendtPensjonsgivendeInntekt)
        assertEquals(BigDecimal("6.04"), opptjening.pensjonspoeng)
    }

    companion object {
        private fun opptjeningshistorikkSpec() =
            OpptjeningshistorikkSpec(
                listOf(OpptjeningSpec(2022, 1000000, Opptjeningstype.PENSJONSGIVENDE_INNTEKT)), foedselsdato()
            )

        private fun foedselsdato() = LocalDate.of(1963, 1, 1)

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
    "personOpptjeningsgrunnlagListe":
     [
        {
            "opptjening": {
                "ar": 2022,
                "pi": 1000000,
                "pia": 772469,
                "pp": 6.04,
                "opptjeningType": {
                    "kode": "PPI",
                    "er_gyldig": true
                },
                "maksUforegrad": 0,
                "bruk": false,
                "opptjeningTypeListe": [
                    "java.util.ArrayList",
                    []
                ]
            },
            "fodselsdato": -220932000000
        }
    ]
}""".trimIndent()
                )
        }
    }
}
