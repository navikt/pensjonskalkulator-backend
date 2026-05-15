package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(EpsController::class)
@Import(MockSecurityConfiguration::class)
class EpsControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var service: EpsService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
            every { pidExtractor.pid() } returns pid
            every { auditor.audit(any(), any(), any()) } returns Unit
        }

        context("suksess") {
            should("gi familierelasjon") {
                every { service.nyligsteRelasjon(any()) } returns Familierelasjon(
                    pid,
                    fom = LocalDate.of(2021, 1, 1),
                    relasjonstype = Relasjonstype.SAMBOER,
                    relasjonPersondata = null
                )

                mvc.perform(
                    post(URL)
                        .with(csrf())
                        .content(REQUEST_BODY)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk())
                    .andExpect(content().json(OK_RESPONSE_BODY))
            }
        }

        context("tilgang nektet") {
            should("gi statuskode 'Forbidden' og problembeskrivelse") {
                every {
                    service.nyligsteRelasjon(any())
                } throws AccessDeniedException("Tilgang til EPS nektet: Egen ansatt")

                mvc.perform(
                    post(URL)
                        .with(csrf())
                        .content(REQUEST_BODY)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isForbidden())
                    .andExpect(content().json(PROBLEM_RESPONSE_BODY))
            }
        }
    }

    private companion object {
        private const val URL = "/api/intern/v1/eps"

        @Language("json")
        private const val REQUEST_BODY = """{
            "sivilstatus": "SAMBOER",
            "bakgrunn": "abc"
        }"""

        @Language("json")
        private const val OK_RESPONSE_BODY = """{
            "pid": "12906498357",
            "fom": "2021-01-01",
            "relasjonstype": "SAMBOER",
            "relasjonPersondata": null
        }"""

        @Language("json")
        private const val PROBLEM_RESPONSE_BODY = """{
            "pid": null,
            "fom": null,
            "relasjonstype": "UKJENT",
            "relasjonPersondata": null,
            "problem": {
              "type": "TILGANG_NEKTET",
              "beskrivelse": "Tilgang til EPS nektet: Egen ansatt"
            }
        }"""
    }
}