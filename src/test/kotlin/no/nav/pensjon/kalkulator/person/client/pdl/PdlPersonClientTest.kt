package no.nav.pensjon.kalkulator.person.client.pdl

import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import okhttp3.mockwebserver.MockResponse
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
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
class PdlPersonClientTest : WebClientTest() {

    private lateinit var client: PdlPersonClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        `when`(traceAid.callId()).thenReturn("id1")
        client = PdlPersonClient(baseUrl(), webClientBuilder, traceAid, retryAttempts = "1")
        arrangeSecurityContext()
    }

    @Test
    fun `fetchPerson uses supplied PID in request to PDL`() {
        arrange(okPersonaliaResponse())

        client.fetchPerson(pid, fetchFulltNavn = false)

        ByteArrayOutputStream().use {
            val request = takeRequest()
            request.body.copyTo(it)
            assertEquals("B353", request.getHeader("behandlingsnummer"))
            assertEquals("PEN", request.getHeader("tema"))
            assertEquals(
                """{
	"query": "query(${"$"}ident: ID!) { hentPerson(ident: ${"$"}ident) { navn(historikk: false) { fornavn }, foedselsdato { foedselsdato }, sivilstand(historikk: false) { type } } }",
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
        arrange(okPersonaliaResponse())

        val response: Person = client.fetchPerson(pid, fetchFulltNavn = false)!!

        assertEquals("Ola-Kari", response.navn)
        assertEquals(LocalDate.of(1963, 12, 31), response.foedselsdato)
        assertTrue(response.harFoedselsdato)
        assertEquals(Sivilstand.UGIFT, response.sivilstand)
    }

    @Test
    fun `fetchPerson returns partial person when receiving partial graphql-response`() {
        arrange(partialPersonaliaResponse())

        val response: Person = client.fetchPerson(pid, fetchFulltNavn = false)!!

        assertEquals("Ola-Kari", response.navn)
        assertEquals(LocalDate.MIN, response.foedselsdato)
        assertFalse(response.harFoedselsdato)
        assertEquals(Sivilstand.UOPPGITT, response.sivilstand)
    }

    @Test
    fun `fetchPerson retries in case of server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okPersonaliaResponse())

        val response: Person = client.fetchPerson(pid, fetchFulltNavn = false)!!

        assertEquals("Ola-Kari", response.navn)
        assertEquals(Sivilstand.UGIFT, response.sivilstand)
    }

    @Test
    fun `fetchPerson does not retry in case of client error`() {
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        val exception = assertThrows(EgressException::class.java) { client.fetchPerson(pid, fetchFulltNavn = false) }

        assertEquals("My bad", exception.message)
    }

    @Test
    fun `fetchPerson handles server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil")) // for retry

        val exception = assertThrows<EgressException> { client.fetchPerson(pid, fetchFulltNavn = false) }

        assertEquals("Failed calling ${baseUrl()}/graphql", exception.message)
        assertEquals("Feil", (exception.cause as EgressException).message)
    }

    @Test
    fun `fetchPerson handles extended response`() {
        arrange(extendedResponse())
        // extension data is only logged, so just check that no exception occurs:
        assertDoesNotThrow { client.fetchPerson(pid, fetchFulltNavn = false) }
    }

    @Test
    fun `fetchPerson handles person ikke funnet`() {
        arrange(personIkkeFunnetResponse())
        val exception = assertThrows<NotFoundException> { client.fetchPerson(pid, fetchFulltNavn = false) }
        assertEquals("person", exception.message)
    }

    @Test
    fun `fetchAdressebeskyttelse returns adressebeskyttelsesgradering when OK response`() {
        arrange(okAdressebeskyttelseResponse())

        val response: Person = client.fetchAdressebeskyttelse(pid)!!

        assertEquals(AdressebeskyttelseGradering.STRENGT_FORTROLIG, response.adressebeskyttelse)
    }

    companion object {

        // Actual response from PDL in Q2:
        @Language("JSON")
        private const val PERSONALIA_JSON = """{
              "data": {
                "hentPerson": {
                  "navn": [
                    {
                      "fornavn": "Ola-Kari"
                    }
                  ],
                  "foedselsdato": [
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
            }"""

        @Language("JSON")
        private const val PARTIAL_PERSONALIA_JSON = """{
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
            }"""

        @Language("JSON")
        private const val EXTENSION_JSON = """{
              "data": {
                "hentPerson": {
                  "navn": [
                    {
                      "fornavn": "Ola-Kari"
                    }
                  ],
                  "sivilstand": null
                }
              },
              "extensions": {
              	"warnings": [{
              			"query": "N/A",
              			"id": "deprecated_token",
              			"code": null,
              			"message": "bruk av deprecated token",
              			"details": "STS token er markert som deprecated, vi oppfordrer å bytte til AzureAD system token"
              		},
              		{
              			"query": "hentPerson",
              			"id": "tilgangsstyring",
              			"code": "mangler_opplysninger_i_katalog",
              			"message": "Behandling mangler opplysningstyper",
              			"details": {
              				"missing": [
              					"INNFLYTTING_TIL_NORGE",
              					"STATSBORGERSKAP_V1"
              				]
              			}
              		}
              	]
              }
            }"""

        @Language("JSON")
        private const val ADRESSEBESKYTTELSE_JSON = """{
              "data": {
                "hentPerson": {
                  "adressebeskyttelse": [
                    {
                      "gradering": "STRENGT_FORTROLIG"
                    }
                  ]
                }
              }
            }"""

        @Language("JSON")
        private const val PERSON_IKKE_FUNNET_JSON = """{
    "errors": [
        {
            "message": "Fant ikke person",
            "locations": [
                {
                    "line": 1,
                    "column": 22
                }
            ],
            "path": [
                "hentPerson"
            ],
            "extensions": {
                "code": "not_found",
                "classification": "ExecutionAborted"
            }
        }
    ],
    "data": {
        "hentPerson": null
    }
}"""

        private fun okPersonaliaResponse(): MockResponse =
            jsonResponse(HttpStatus.OK).setBody(PERSONALIA_JSON.trimIndent())

        private fun okAdressebeskyttelseResponse(): MockResponse =
            jsonResponse(HttpStatus.OK).setBody(ADRESSEBESKYTTELSE_JSON.trimIndent())

        private fun partialPersonaliaResponse(): MockResponse =
            jsonResponse(HttpStatus.OK).setBody(PARTIAL_PERSONALIA_JSON.trimIndent())

        private fun extendedResponse(): MockResponse =
            jsonResponse(HttpStatus.OK).setBody(EXTENSION_JSON.trimIndent())

        private fun personIkkeFunnetResponse(): MockResponse =
            jsonResponse(HttpStatus.OK).setBody(PERSON_IKKE_FUNNET_JSON.trimIndent())
    }
}
