package no.nav.pensjon.kalkulator.person.client.pdl

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.mock.TestObjects.pid1
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class PdlPersonClientTest : FunSpec({
    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    beforeSpec {
        Arrange.security()
        server = MockWebServer().also { it.start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("fetchPerson uses supplied PID in request to PDL") {
        server!!.arrangeOkJsonResponse(PdlResponse.PERSONALIA_JSON)

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            client.fetchPerson(pid = pid1, fetchFulltNavn = false)

            ByteArrayOutputStream().use {
                server.takeRequest().apply {
                    body.copyTo(it)
                    getHeader("behandlingsnummer") shouldBe "B353"
                    getHeader("tema") shouldBe "PEN"
                }
                it.toString(StandardCharsets.UTF_8) shouldBe
                        """{
	"query": "query(${"$"}ident: ID!) { hentPerson(ident: ${"$"}ident) { navn(historikk: false) { fornavn }, foedselsdato { foedselsdato }, sivilstand(historikk: false) { type } } }",
	"variables": {
		"ident": "22925399748"
	}
}"""
            }

        }
    }

    test("fetchAdressebeskyttelse returns adressebeskyttelsesgradering when OK response") {
        server!!.arrangeOkJsonResponse(PdlResponse.ADRESSEBESKYTTELSE_JSON)

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            val response: Person = client.fetchAdressebeskyttelse(pid1)!!

            with(response) {
                adressebeskyttelse shouldBe AdressebeskyttelseGradering.STRENGT_FORTROLIG
            }
        }
    }

    test("fetchPerson returns person when OK response") {
        server!!.arrangeOkJsonResponse(PdlResponse.PERSONALIA_JSON)

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            val response: Person = client.fetchPerson(pid1, fetchFulltNavn = false)!!

            with(response) {
                navn shouldBe "Ola-Kari"
                foedselsdato shouldBe LocalDate.of(1963, 12, 31)
                harFoedselsdato shouldBe true
                sivilstand shouldBe Sivilstand.UGIFT
            }
        }
    }

    test("fetchPerson returns partial person when receiving partial graphql-response") {
        server!!.arrangeOkJsonResponse(PdlResponse.PARTIAL_PERSONALIA_JSON)

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            val response: Person = client.fetchPerson(pid1, fetchFulltNavn = false)!!

            with(response) {
                navn shouldBe "Ola-Kari"
                foedselsdato shouldBe LocalDate.MIN
                harFoedselsdato shouldBe false
                sivilstand shouldBe Sivilstand.UOPPGITT
            }
        }
    }

    test("fetchPerson handles extended response") {
        server!!.arrangeOkJsonResponse(PdlResponse.EXTENSION_JSON)

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            // extension data is only logged, so just check that no exception occurs:
            shouldNotThrowAny { client.fetchPerson(pid1, fetchFulltNavn = false)!! }
        }
    }

    test("fetchPerson handles person ikke funnet") {
        server!!.arrangeOkJsonResponse(PdlResponse.PERSON_IKKE_FUNNET_JSON)

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            shouldThrow<NotFoundException> {
                client.fetchPerson(
                    pid1,
                    fetchFulltNavn = false
                )
            }.message shouldBe "person"
        }
    }

    test("fetchPerson handles server error") {
        server!!.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            val e = shouldThrow<EgressException> { client.fetchPerson(pid1, fetchFulltNavn = false) }
            e.message shouldBe "Failed calling ${baseUrl}/graphql"
            (e.cause as EgressException).message shouldBe "Feil"
        }
    }

    test("fetchPerson does not retry in case of client error") {
        server!!.arrangeResponse(HttpStatus.BAD_REQUEST, "My bad")
        // No 2nd response arranged, since no retry

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "0"
            )

            shouldThrow<EgressException> { client.fetchPerson(pid1, fetchFulltNavn = false) }.message shouldBe "My bad"
        }
    }

    test("fetchPerson retries in case of server error") {
        server!!.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server.arrangeOkJsonResponse(PdlResponse.PERSONALIA_JSON)

        Arrange.webClientContextRunner().run {
            val client = PdlPersonClient(
                baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid,
                retryAttempts = "1"
            )

            val response: Person = client.fetchPerson(pid1, fetchFulltNavn = false)!!

            with(response) {
                navn shouldBe "Ola-Kari"
                sivilstand shouldBe Sivilstand.UGIFT
            }
        }
    }
})

object PdlResponse {

    // Actual response from PDL in Q2:
    @Language("JSON")
    const val PERSONALIA_JSON = """{
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
    const val PARTIAL_PERSONALIA_JSON = """{
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
    const val EXTENSION_JSON = """{
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
    const val ADRESSEBESKYTTELSE_JSON = """{
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
    const val PERSON_IKKE_FUNNET_JSON = """{
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
}
