package no.nav.pensjon.kalkulator.person.client.pdl

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import okhttp3.mockwebserver.MockResponse
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class PdlPersonClientTest : WebClientTest() {

    private lateinit var client: PdlPersonClient

    @BeforeEach
    fun initialize() {
        client = PdlPersonClient(baseUrl(), WebClient.create())
    }

    @Test
    fun `getPerson uses supplied PID in request to PDL`() {
        arrangeSecurityContext()
        arrange(okResponse())

        client.getPerson(Pid("12906498357"))!!

        ByteArrayOutputStream().use {
            val request = takeRequest()
            request.body.copyTo(it)
            assertEquals("B353", request.getHeader("behandlingsnummer"))
            assertEquals("PEN", request.getHeader("tema"))
            assertEquals(
                """{
	"query": "query(${"$"}ident: ID!) { hentPerson(ident: ${"$"}ident) { navn(historikk: false) { fornavn }, sivilstand(historikk: true) { type } } }",
	"variables": {
		"ident": "12906498357"
	}
}""",
                it.toString(StandardCharsets.UTF_8)
            )
        }
    }

    @Test
    fun `getPerson returns person when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response: Person = client.getPerson(Pid("12906498357"))!!

        assertEquals("Ola-Kari", response.fornavn)
        assertEquals(Sivilstand.UGIFT, response.sivilstand)
    }

    @Test
    fun `getPerson returns partial person when receiving partial graphql-response`() {
        arrangeSecurityContext()
        arrange(partialResponse())

        val response: Person = client.getPerson(Pid("12906498357"))!!

        assertEquals("Ola-Kari", response.fornavn)
        assertEquals(null, response.sivilstand)
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
            // Actual response from PDL in Q2:
            @Language("JSON")
            val body = """{
              "data": {
                "hentPerson": {
                  "navn": [
                    {
                      "fornavn": "Ola-Kari"
                    }
                  ],
                  "sivilstand": [
                    {
                      "type": "UGIFT"
                    }
                  ]
                }
              }
            }""".trimIndent()
            return jsonResponse(HttpStatus.OK)
                .setBody(body)
        }

        private fun partialResponse(): MockResponse {
            @Language("JSON")
            val body = """{
              "data": {
                "hentPerson": {
                  "navn": [
                    {
                      "fornavn": "Ola-Kari"
                    }
                  ],
                  "sivilstand": null
                }
              }
            }""".trimIndent()
            return jsonResponse(HttpStatus.OK)
                .setBody(body)
        }
    }
}
