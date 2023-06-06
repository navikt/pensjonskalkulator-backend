package no.nav.pensjon.kalkulator.avtale.client.pen

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

class PenPensjonsavtaleClientTest : WebClientTest() {

    private lateinit var client: PenPensjonsavtaleClient

    @BeforeEach
    fun initialize() {
        client = PenPensjonsavtaleClient(baseUrl(), WebClient.create())
    }

    @Test
    fun `fetchAvtaler handles single pensjonsavtale`() {
        arrangeSecurityContext()
        arrange(okResponse(enkeltAvtale()))

        val response: Pensjonsavtaler = client.fetchAvtaler(Pid("12906498357"))

        val avtale = response.liste[0]
        assertEquals("avtale1", avtale.navn)
        assertEquals(LocalDate.of(1992, 3, 4), avtale.fom)
        assertEquals(LocalDate.of(2010, 11, 12), avtale.tom)
    }

    @Test
    fun `fetchAvtaler handles several pensjonsavtaler and handles evigvarende avtale`() {
        arrangeSecurityContext()
        arrange(okResponse(toAvtaler()))

        val avtaler = client.fetchAvtaler(Pid("12906498357"))

        val tidsbegrenset = avtaler.liste.first { it.navn == "tidsbegrenset" }
        assertEquals(LocalDate.of(1992, 3, 4), tidsbegrenset.fom)
        assertEquals(LocalDate.of(2010, 11, 12), tidsbegrenset.tom)
        val evigvarende = avtaler.liste.first { it.navn == "evigvarende" }
        assertEquals(LocalDate.of(2010, 11, 13), evigvarende.fom)
        assertNull(evigvarende.tom)
    }

    companion object {

        private fun arrangeSecurityContext() {
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

            SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
                TestingAuthenticationToken("TEST_USER", null),
                EgressTokenSuppliersByService(mapOf())
            )
        }

        private fun okResponse(avtale: String) =
            jsonResponse(HttpStatus.OK).setBody(avtale)

        @Language("json")
        private fun enkeltAvtale() = """{
            "avtaler": [
                        {
                            "navn": "avtale1",
                            "fom": "1992-03-04",
                            "tom": "2010-11-12"
                        }
                    ]
        }"""

        @Language("json")
        private fun toAvtaler() = """{
            "avtaler": [
                        {
                            "navn": "tidsbegrenset",
                            "fom": "1992-03-04",
                            "tom": "2010-11-12"
                        },
                        {
                            "navn": "evigvarende",
                            "fom": "2010-11-13",
                            "tom": null
                        }
                    ]
        }"""
    }
}
