package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class TpTjenestepensjonClientTest : WebClientTest() {

    private lateinit var client: TpTjenestepensjonClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = TpTjenestepensjonClient(
            baseUrl = baseUrl(),
            webClientBuilder = webClientBuilder,
            traceAid = traceAid,
            retryAttempts = RETRY_ATTEMPTS
        )

        arrangeSecurityContext()
    }

    @Test
    fun `'harTjenestepensjonsforhold' gir 'true' naar personen har tjenestepensjonsforhold`() {
        arrange(okStatusResponse(true))
        assertTrue(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `'erApoteker' gir 'true' naar personen er medlem av Apotekerforeningen`() {
        arrange(okApotekerResponse(true))
        assertTrue(client.erApoteker(pid))
    }

    @Test
    fun `'erApoteker' gir 'false' naar personen ikke er medlem av Apotekerforeningen`() {
        arrange(okApotekerResponse(false))
        assertFalse(client.erApoteker(pid))
    }

    @Test
    fun `'tjenestepensjon' gir forhold-liste naar personen har tjenestepensjonsforhold`() {
        arrange(okForholdResponse())
        val tjenestepensjonsforhold = client.tjenestepensjon(pid)

        with(tjenestepensjonsforhold.forholdList[0]) {
            assertEquals("3100", ordning)
            assertEquals(1, ytelser.size)
            assertNull(datoSistOpptjening)
            with(ytelser[0]) {
                assertEquals("ALDER", type)
                assertEquals(LocalDate.of(2021, 2, 3), this.datoInnmeldtYtelseFom)
                assertEquals(LocalDate.of(2022, 7, 16), this.datoYtelseIverksattFom)
                assertEquals(LocalDate.of(2027, 8, 15), this.datoYtelseIverksattTom)
            }
        }
    }

    @Test
    fun `'tjenestepensjon' gir forhold-liste naar personen har hatt tjenestepensjonsforhold`() {
        arrange(okTjenestepensjonsforholdResponse())
        val tjenestepensjonsforhold = client.tjenestepensjonsforhold(pid)

       assertEquals(listOf("Utviklers pensjonskasse", "Pensjonskasse for folk flest"), tjenestepensjonsforhold.tpOrdninger)
    }

    @Test
    fun `'harTjenestepensjonsforhold' gir 'false' naar personen ikke har tjenestepensjonsforhold`() {
        arrange(okStatusResponse(false))
        assertFalse(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `'harTjenestepensjonsforhold' gjentar kallet ved serverfeil`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okStatusResponse(true))

        assertTrue(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `'harTjenestepensjonsforhold' gjentar ikke kallet ved klientfeil`() {
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        val exception = assertThrows(EgressException::class.java) { client.harTjenestepensjonsforhold(pid, dato) }

        assertEquals("My bad", exception.message)
    }

    @Test
    fun `'harTjenestepensjonsforhold' gir fornuftig feimelding ved serverfeil`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil")) // for retry

        val exception = assertThrows(EgressException::class.java) { client.harTjenestepensjonsforhold(pid, dato) }

        assertEquals(
            "Failed calling ${baseUrl()}/api/tjenestepensjon/haveYtelse?date=2023-02-01&ytelseType=ALDER&ordningType=TPOF",
            exception.message
        )
        assertEquals("Feil", (exception.cause as EgressException).message)
    }

    companion object {
        private const val RETRY_ATTEMPTS = "1"
        private val dato = LocalDate.of(2023, 2, 1)

        @Language("json")
        private fun apotekerResponseBody(value: Boolean) =
            """{
    "harLopendeForholdApotekerforeningen": $value,
    "harAndreLopendeForhold": true
}"""

        @Language("json")
        private fun statusResponseBody(value: Boolean) =
            """{
                 "value": $value
             }
             """

        @Language("json")
        private fun forholdResponseBody() =
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

        private const val tjenestepensjonsForholdResponsBody = """
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

        private fun okApotekerResponse(value: Boolean) = jsonResponse().setBody(apotekerResponseBody(value).trimIndent())

        private fun okStatusResponse(value: Boolean) = jsonResponse().setBody(statusResponseBody(value).trimIndent())

        private fun okForholdResponse() = jsonResponse().setBody(forholdResponseBody().trimIndent())

        private fun okTjenestepensjonsforholdResponse() = jsonResponse().setBody(tjenestepensjonsForholdResponsBody.trimIndent())
    }
}
