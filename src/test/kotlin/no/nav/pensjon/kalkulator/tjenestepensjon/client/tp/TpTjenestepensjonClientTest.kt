package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import no.nav.pensjon.kalkulator.tjenestepensjon.Forhold
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjon
import no.nav.pensjon.kalkulator.tjenestepensjon.Ytelse
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClientTestObjects.TJENESTEPENSJON
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClientTestObjects.TJENESTEPENSJONSFORHOLD
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClientTestObjects.apotekerResponseBody
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClientTestObjects.dato
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClientTestObjects.statusResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class TpTjenestepensjonClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        TpTjenestepensjonClient(
            baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            traceAid,
            retryAttempts = "1"
        )

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("'harTjenestepensjonsforhold' gir 'true' når personen har tjenestepensjonsforhold") {
        server?.arrangeOkJsonResponse(statusResponseBody(true))

        Arrange.webClientContextRunner().run {
            client(context = it).harTjenestepensjonsforhold(pid, dato) shouldBe true
        }
    }

    test("'erApoteker' gir 'true' når personen er medlem av Apotekerforeningen") {
        server?.arrangeOkJsonResponse(apotekerResponseBody(true))

        Arrange.webClientContextRunner().run {
            client(context = it).erApoteker(pid) shouldBe true
        }
    }

    test("'erApoteker' gir 'false' når personen ikke er medlem av Apotekerforeningen") {
        server?.arrangeOkJsonResponse(apotekerResponseBody(false))

        Arrange.webClientContextRunner().run {
            client(context = it).erApoteker(pid) shouldBe false
        }
    }

    test("'erApoteker' gir 'false' når personen ikke finnes i TP-registeret") {
        server?.arrangeResponse(HttpStatus.NOT_FOUND, PERSON_IKKE_FUNNET_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            client(context = it).erApoteker(pid) shouldBe false
        }
    }

    test("'tjenestepensjon' gir forhold-liste når personen har tjenestepensjonsforhold") {
        server?.arrangeOkJsonResponse(TJENESTEPENSJON)

        Arrange.webClientContextRunner().run {
            val tjenestepensjonsforhold = client(context = it).tjenestepensjon(pid)

            tjenestepensjonsforhold shouldBe Tjenestepensjon(
                forholdList = listOf(
                    Forhold(
                        ordning = "3100",
                        ytelser = listOf(
                            Ytelse(
                                type = "ALDER",
                                datoInnmeldtYtelseFom = LocalDate.of(2021, 2, 3),
                                datoYtelseIverksattFom = LocalDate.of(2022, 7, 16),
                                datoYtelseIverksattTom = LocalDate.of(2027, 8, 15)
                            )
                        ),
                        datoSistOpptjening = null
                    )
                )
            )
        }
    }

    test("'tjenestepensjon' gir forhold-liste når personen har hatt tjenestepensjonsforhold") {
        server?.arrangeOkJsonResponse(TJENESTEPENSJONSFORHOLD)

        Arrange.webClientContextRunner().run {
            client(context = it).tjenestepensjonsforhold(pid).tpOrdninger shouldBe
                    listOf("Utviklers pensjonskasse", "Pensjonskasse for folk flest")
        }
    }

    test("'harTjenestepensjonsforhold' gir 'false' når personen ikke har tjenestepensjonsforhold") {
        server?.arrangeOkJsonResponse(statusResponseBody(false))

        Arrange.webClientContextRunner().run {
            client(context = it).harTjenestepensjonsforhold(pid, dato) shouldBe false
        }
    }

    test("'harTjenestepensjonsforhold' gjentar kallet ved serverfeil") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeOkJsonResponse(statusResponseBody(true))

        Arrange.webClientContextRunner().run {
            client(context = it).harTjenestepensjonsforhold(pid, dato) shouldBe true
        }
    }

    test("'harTjenestepensjonsforhold' gjentar ikke kallet ved klientfeil") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, "My bad")
        // No 2nd response arranged, since no retry

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> {
                client(context = it).harTjenestepensjonsforhold(pid, dato)
            }.message shouldBe "My bad"
        }
    }

    test("'harTjenestepensjonsforhold' gir fornuftig feilmelding ved serverfeil") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil") // for retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> { client(context = it).harTjenestepensjonsforhold(pid, dato) }

            with(exception) {
                message shouldBe "Failed calling $baseUrl/api/tjenestepensjon/haveYtelse?date=2023-02-01&ytelseType=ALDER&ordningType=TPOF"
                (cause as EgressException).message shouldBe "Feil"
            }
        }
    }
})

@Language("json")
private const val PERSON_IKKE_FUNNET_RESPONSE_BODY =
    """{
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Person ikke funnet.",
    "instance": "/api/tjenestepensjon/medlem/afp/apotekerforeningen/ersisteforhold"
}"""

object TpTjenestepensjonClientTestObjects {

    val dato = LocalDate.of(2023, 2, 1)

    @Language("json")
    fun apotekerResponseBody(value: Boolean) =
        """{
    "harLopendeForholdApotekerforeningen": $value,
    "harAndreLopendeForhold": true
}"""

    @Language("json")
    fun statusResponseBody(value: Boolean) =
        """{
                 "value": $value
             }
             """

    @Language("json")
    const val TJENESTEPENSJON =
        """{
    "forhold": [
        {
            "ordning": "3100",
            "ytelser": [
                {
                    "type": "ALDER",
                    "datoInnmeldtYtelseFom": "2021-02-03",
                    "datoYtelseIverksattFom": "2022-07-16",
                    "datoYtelseIverksattTom": "2027-08-15",
                    "_links": {
                        "self": {
                            "href": "https://tp-q2.dev.intern.nav.no/api/tjenestepensjon/forhold/80000470763/ytelse/22584987"
                        },
                        "edit": {
                            "href": "https://tp-q2.dev.intern.nav.no/api/tjenestepensjon/forhold/3100/ytelse/22584987"
                        },
                        "delete": {
                            "href": "https://tp-q2.dev.intern.nav.no/api/tjenestepensjon/forhold/3100/ytelse/22584987"
                        }
                    }
                }
            ],
            "createdBy": "srvpensjon",
            "updatedBy": "srvpensjon",
            "kilde": "PP01",
            "datoSistOpptjening": null,
            "_links": {
                "ordning": {
                    "href": "https://tp-q2.dev.intern.nav.no/api/ordninger/3100"
                },
                "addYtelse": {
                    "href": "https://tp-q2.dev.intern.nav.no/api/tjenestepensjon/forhold/3100/ytelse"
                }
            }
        }
    ],
    "_links": {
        "addForhold": {
            "href": "https://tp-q2.dev.intern.nav.no/api/tjenestepensjon/forhold"
        }
    }
}"""

    @Language("json")
    const val TJENESTEPENSJONSFORHOLD = """
            {
  "fnr": "***********",
  "forhold": [
    {
      "samtykkeSimulering": false,
      "kilde": "PP01",
      "tpNr": "3010",
      "ordning": {
        "navn": "Utviklers pensjonskasse",
        "tpNr": "4",
        "orgNr": "5",
        "tssId": "6"
      },
      "harSimulering": false,
      "harUtlandsPensjon": false,
      "datoSamtykkeGitt": null,
      "ytelser": [
        {
          "datoInnmeldtYtelseFom": "2020-01-01",
          "ytelseType": "ALDER",
          "datoYtelseIverksattFom": "2020-01-01",
          "datoYtelseIverksattTom": "2020-12-31",
          "changeStamp": {
            "createdBy": "Dummy User",
            "createdDate": "2022-09-20T13:30:00",
            "updatedBy": "Dummy User",
            "updatedDate": "2022-09-20T13:30:00"
          }
        }
      ],
      "changeStampDate": {
        "createdBy": "Dummy User",
        "createdDate": "2022-09-20T13:30:00",
        "updatedBy": "Dummy User",
        "updatedDate": "2022-09-20T13:30:00"
      }
    },
    {
      "samtykkeSimulering": false,
      "kilde": "PP01",
      "tpNr": "3010",
      "ordning": {
        "navn": "Pensjonskasse for folk flest",
        "tpNr": "1",
        "orgNr": "2",
        "tssId": "3"
      },
      "harSimulering": false,
      "harUtlandsPensjon": false,
      "datoSamtykkeGitt": null,
      "ytelser": [
        {
          "datoInnmeldtYtelseFom": "2020-01-01",
          "ytelseType": "ALDER",
          "datoYtelseIverksattFom": "2020-01-01",
          "datoYtelseIverksattTom": "2020-12-31",
          "changeStamp": {
            "createdBy": "Dummy User",
            "createdDate": "2022-09-20T13:30:00",
            "updatedBy": "Dummy User",
            "updatedDate": "2022-09-20T13:30:00"
          }
        }
      ],
      "changeStampDate": {
        "createdBy": "Dummy User",
        "createdDate": "2022-09-20T13:30:00",
        "updatedBy": "Dummy User",
        "updatedDate": "2022-09-20T13:30:00"
      }
    }
  ]
}
        """
}
