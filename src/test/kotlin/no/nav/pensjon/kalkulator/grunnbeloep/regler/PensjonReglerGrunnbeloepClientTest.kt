package no.nav.pensjon.kalkulator.grunnbeloep.regler


import no.nav.pensjon.kalkulator.grunnbeloep.regler.dto.SatsResponse
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.regler.ReglerConfiguration
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

class PensjonReglerGrunnbeloepClientTest : WebClientTest() {

    private lateinit var client: PensjonReglerGrunnbeloepClient

    @BeforeEach
    fun initialize() {
        client = PensjonReglerGrunnbeloepClient(baseUrl(), WebClient.create(), ReglerConfiguration().objectMapper())
    }

    @Test
    fun getGrunnbeloep_returns_satsResultater_when_ok_response() {
        prepare(okResponse())

        val response: SatsResponse = client.getGrunnbeloep("")

        val satsResultat = response.satsResultater?.get(0)!!
        assertEquals(Date(1651399200000L), satsResultat.fom)
        assertEquals(Date(253402254000000L), satsResultat.tom)
        assertEquals(111477.0, satsResultat.verdi)
    }

    companion object {

        private fun okResponse(): MockResponse {
            // Actual response from pensjon-regler in Q2:
            return jsonResponse(HttpStatus.OK)
                .setBody(
                    """{
    "satsResultater": ["java.util.ArrayList", [{
        "fom": 1651399200000,
        "tom": 253402254000000,
        "verdi": 111477.0
    }]]
}""".trimIndent()
                )
        }
    }
}
