package no.nav.pensjon.kalkulator.person.client.pdl

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import okhttp3.mockwebserver.MockResponse
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class PdlPersonClientTest : WebClientTest() {

    private lateinit var client: PdlPersonClient

    @Mock
    private lateinit var callIdGenerator: CallIdGenerator

    @BeforeEach
    fun initialize() {
        `when`(callIdGenerator.newId()).thenReturn("id1")
        client = PdlPersonClient(baseUrl(), WebClientConfig().regularWebClient(), callIdGenerator, "1")
    }

    @Test
    fun `fetchPerson uses supplied PID in request to PDL`() {
        arrangeSecurityContext()
        arrange(okResponse())

        client.fetchPerson(pid)

        ByteArrayOutputStream().use {
            val request = takeRequest()
            request.body.copyTo(it)
            assertEquals("B353", request.getHeader("behandlingsnummer"))
            assertEquals("PEN", request.getHeader("tema"))
            assertEquals(
                """{
	"query": "query(${"$"}ident: ID!) { hentPerson(ident: ${"$"}ident) { navn(historikk: false) { fornavn }, foedsel { foedselsdato }, sivilstand(historikk: true) { type } } }",
	"variables": {
		"ident": "12906498357"
	}
}""",
                it.toString(StandardCharsets.UTF_8)
            )
        }
    }

    @Test
    fun `fetchPerson returns person when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response: Person = client.fetchPerson(pid)!!

        assertEquals("Ola-Kari", response.fornavn)
        assertEquals(LocalDate.of(1963, 12, 31), response.foedselsdato)
        assertTrue(response.harFoedselsdato)
        assertEquals(Sivilstand.UGIFT, response.sivilstand)
    }

    @Test
    fun `fetchPerson returns partial person when receiving partial graphql-response`() {
        arrangeSecurityContext()
        arrange(partialResponse())

        val response: Person = client.fetchPerson(pid)!!

        assertEquals("Ola-Kari", response.fornavn)
        assertEquals(LocalDate.MIN, response.foedselsdato)
        assertFalse(response.harFoedselsdato)
        assertEquals(null, response.sivilstand)
    }

    @Test
    fun `fetchPerson retries in case of server error`() {
        arrangeSecurityContext()
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okResponse())

        val response: Person = client.fetchPerson(pid)!!

        assertEquals("Ola-Kari", response.fornavn)
        assertEquals(Sivilstand.UGIFT, response.sivilstand)
    }

    @Test
    fun `fetchPerson does not retry in case of client error`() {
        arrangeSecurityContext()
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        val exception = assertThrows(EgressException::class.java) { client.fetchPerson(pid) }

        assertEquals("My bad", exception.message)
    }

    @Test
    fun `fetchPerson handles server error`() {
        arrangeSecurityContext()
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil")) // for retry

        val exception = assertThrows(EgressException::class.java) { client.fetchPerson(pid) }

        assertEquals("Failed calling ${baseUrl()}/graphql", exception.message)
        assertEquals("Feil", (exception.cause as EgressException).message)
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
                  "foedsel": [
                    {
                      "foedselsdato": "1963-12-31"
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

            return jsonResponse(HttpStatus.OK).setBody(body)
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

            return jsonResponse(HttpStatus.OK).setBody(body)
        }
    }
}
