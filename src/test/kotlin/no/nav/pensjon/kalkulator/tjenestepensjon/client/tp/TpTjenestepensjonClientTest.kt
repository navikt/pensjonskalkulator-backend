package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class TpTjenestepensjonClientTest : WebClientTest() {

    private lateinit var client: TpTjenestepensjonClient

    @Mock
    private lateinit var callIdGenerator: CallIdGenerator

    @BeforeEach
    fun initialize() {
        client =
            TpTjenestepensjonClient(baseUrl(), WebClientConfig().regularWebClient(), callIdGenerator, RETRY_ATTEMPTS)
        arrangeSecurityContext()
    }

    @Test
    fun `harTjenestepensjonsforhold returns true when personen har tjenestepensjonsforhold`() {
        arrange(okResponse(true))
        assertTrue(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `harTjenestepensjonsforhold returns false when personen ikke har tjenestepensjonsforhold`() {
        arrange(okResponse(false))
        assertFalse(client.harTjenestepensjonsforhold(pid, dato))
    }

    @Test
    fun `harTjenestepensjonsforhold retries in case of server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okResponse(true))

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
            "Failed calling ${baseUrl()}/api/tjenestepensjon/haveYtelse?date=2023-02-01&ytelseType=ALDER&ordningType=TPOF",
            exception.message
        )
        assertEquals("Feil", (exception.cause as EgressException).message)
    }

    companion object {
        private const val RETRY_ATTEMPTS = "1"
        private val dato = LocalDate.of(2023, 2, 1)

        @Language("json")
        private fun responseBody(value: Boolean) =
            """{
                 "value": $value
             }
             """

        private fun okResponse(value: Boolean) = jsonResponse().setBody(responseBody(value).trimIndent())
    }
}
