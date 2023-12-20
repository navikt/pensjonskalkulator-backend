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
    fun `harTjenestepensjonsforhold returns true when personen har tjenestepensjonsforhold`() {
        arrange(okStatusResponse(true))
        assertTrue(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `tjenestepensjonsforhold returns forhold when personen har tjenestepensjonsforhold`() {
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
    fun `harTjenestepensjonsforhold returns false when personen ikke har tjenestepensjonsforhold`() {
        arrange(okStatusResponse(false))
        assertFalse(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `harTjenestepensjonsforhold retries in case of server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okStatusResponse(true))

        assertTrue(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `harTjenestepensjonsforhold does not retry in case of client error`() {
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        val exception = assertThrows(EgressException::class.java) { client.harTjenestepensjonsforhold(pid, dato) }

        assertEquals("My bad", exception.message)
    }

    @Test
    fun `harTjenestepensjonsforhold handles server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil")) // for retry

        val exception = assertThrows(EgressException::class.java) { client.harTjenestepensjonsforhold(pid, dato) }

        assertEquals(
            "Failed calling /api/tjenestepensjon/haveYtelse?date=2023-02-01&ytelseType=ALDER&ordningType=TPOF",
            exception.message
        )
        assertEquals("Feil", (exception.cause as EgressException).message)
    }

    companion object {
        private const val RETRY_ATTEMPTS = "1"
        private val dato = LocalDate.of(2023, 2, 1)

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

        private fun okStatusResponse(value: Boolean) = jsonResponse().setBody(statusResponseBody(value).trimIndent())

        private fun okForholdResponse() = jsonResponse().setBody(forholdResponseBody().trimIndent())
    }
}
