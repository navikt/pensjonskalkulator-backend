package no.nav.pensjon.kalkulator.ansatt.enhet.api.v1

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import no.nav.pensjon.kalkulator.ansatt.enhet.AnsattEnhetResult
import no.nav.pensjon.kalkulator.ansatt.enhet.EnhetService
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnhet
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(AnsattEnhetV1Controller::class)
class AnsattEnhetV1ControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var enhetService: EnhetService

    @MockkBean
    private lateinit var traceAid: TraceAid

    init {
        fun arrangeTrace() {
            every { traceAid.begin() } returns Unit
            every { traceAid.end() } returns Unit
        }

        fun verifyTrace() {
            verify { traceAid.begin() }
            verify { traceAid.end() }
        }

        context("success - service responds") {
            should("return status '200 OK' with a list of enheter") {
                every { enhetService.tjenestekontorEnhetListe() } returns AnsattEnhetResult(
                    enhetListe = listOf(
                        TjenestekontorEnhet(id = "123", navn = "Oslo", nivaa = "1"),
                        TjenestekontorEnhet(id = "456", navn = "Bergen", nivaa = "2")
                    )
                )
                arrangeTrace()

                mockMvc.get(URL)
                    .andExpect { status { isOk() } }
                    .andReturn()
                    .response.contentAsString shouldBe
                        """{"enhetListe":[{"id":"123","navn":"Oslo"},{"id":"456","navn":"Bergen"}]}"""

                verifyTrace()
            }
        }

        context("failure - ansatt not found") {
            should(" return status '404 Not Found' with problem description as content") {
                every { enhetService.tjenestekontorEnhetListe() } returns AnsattEnhetResult(
                    enhetListe = emptyList(),
                    problem = Problem(
                        type = ProblemType.PERSON_IKKE_FUNNET,
                        beskrivelse = "User with ID X123456 not found"
                    )
                )
                arrangeTrace()

                mockMvc.get(URL)
                    .andExpect { status { isNotFound() } }
                    .andReturn()
                    .response.contentAsString shouldBe
                        """{"enhetListe":[],"problem":{"kode":"ANSATT_IKKE_FUNNET","beskrivelse":"User with ID X123456 not found"}}"""

                verifyTrace()
            }
        }

        context("failure - exception occurs") {
            should(" return status '500 Internal Server Error' with exception message as content") {
                every { enhetService.tjenestekontorEnhetListe() } throws RuntimeException("Internal error")
                arrangeTrace()

                mockMvc.get(URL)
                    .andExpect { status { isInternalServerError() } }
                    .andReturn()
                    .response.contentAsString shouldBe
                        """{"enhetListe":[],"problem":{"kode":"SERVERFEIL","beskrivelse":"Internal error"}}"""

                verifyTrace()
            }
        }

        context("failure - empty response") {
            should("return status '200 OK' with empty list as content") {
                every { enhetService.tjenestekontorEnhetListe() } returns AnsattEnhetResult(enhetListe = emptyList())
                arrangeTrace()

                mockMvc.get(URL)
                    .andExpect { status { isOk() } }
                    .andReturn()
                    .response.contentAsString shouldBe """{"enhetListe":[]}"""

                verifyTrace()
            }
        }
    }
}

private const val URL = "/api/intern/v1/enheter"
