package no.nav.pensjon.kalkulator.person.client.pdl

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Land
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
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
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class PdlPersonClientTest : WebClientTest() {

    private lateinit var client: PdlPersonClient

    @BeforeEach
    fun initialize() {
        client = PdlPersonClient(baseUrl(), WebClient.create())
    }

    @Test
    fun `getPerson returns person when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response: Person = client.getPerson(Pid("12906498357"))

        ByteArrayOutputStream().use {
            takeRequest().body.copyTo(it)
            assertEquals(
                """{
	"query": "query(${"$"}ident: ID!) { hentPerson(ident: ${"$"}ident) { foedsel { foedselsdato }, statsborgerskap { land }, sivilstand(historikk: true) { type } } }",
	"variables": {
		"ident": "12906498357"
	}
}""",
                it.toString(StandardCharsets.UTF_8)
            )
        }

        assertEquals(LocalDate.of(1971, 11, 12), response.foedselsdato)
        assertEquals(Land.NORGE, response.statsborgerskap)
        assertEquals(Sivilstand.UGIFT, response.sivilstand)
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
            return jsonResponse(HttpStatus.OK)
                .setBody(
                    """{
    "data": {
        "hentPerson": {
            "foedsel": [
                {
                    "foedselsdato": "1971-11-12"
                }
            ],
            "statsborgerskap": [
                {
                    "land": "NOR"
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
                )
        }
    }
}
