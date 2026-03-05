package no.nav.pensjon.kalkulator.lagring.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.lagring.LagreSimuleringResponse
import no.nav.pensjon.kalkulator.lagring.LagreSimuleringService
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(LagreSimuleringController::class)
@Import(MockSecurityConfiguration::class)
class LagreSimuleringControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var service: LagreSimuleringService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
            every { pidExtractor.pid() } returns pid
            every { adresseService.adressebeskyttelseGradering(any()) } returns AdressebeskyttelseGradering.UGRADERT
            every { auditor.audit(any(), any()) } returns Unit
        }

        should("lagre simulering og returnere brev-respons") {
            every { service.lagreSimulering(any()) } returns lagreSimuleringResponse()

            mvc.perform(
                post(URL)
                    .with(csrf())
                    .content(REQUEST_BODY)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY))
        }

        should("returnere 503 ved ekstern feil mot Skribenten") {
            every { service.lagreSimulering(any()) } throws EgressException(
                message = "Skribenten er utilgjengelig",
                statusCode = HttpStatus.SERVICE_UNAVAILABLE
            )

            mvc.perform(
                post(URL)
                    .with(csrf())
                    .content(REQUEST_BODY)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isServiceUnavailable())
        }

        should("returnere 500 ved intern klientfeil") {
            every { service.lagreSimulering(any()) } throws EgressException(
                message = "Intern klientfeil",
                statusCode = HttpStatus.BAD_REQUEST
            )

            mvc.perform(
                post(URL)
                    .with(csrf())
                    .content(REQUEST_BODY)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isInternalServerError())
        }
    }

    private companion object {
        private const val URL = "/api/intern/v1/lagre-simulering"
        private const val BREV_ID = "brev-123"
        private const val SAK_ID = "sak-456"

        @Language("json")
        private val REQUEST_BODY = """{
            "alderspensjonListe": [
                { "alderAar": 67, "beloep": 250000 }
            ],
            "vilkaarsproevingsresultat": {
                "erInnvilget": true
            }
        }""".trimIndent()

        @Language("json")
        private val RESPONSE_BODY = """{
            "brevId": "$BREV_ID",
            "sakId": "$SAK_ID",
            "brevDevQ2Url": "https://pensjon-skribenten-web-q2.intern.dev.nav.no/saksnummer/$SAK_ID/brev/$BREV_ID"
        }""".trimIndent()

        private fun lagreSimuleringResponse() =
            LagreSimuleringResponse(
                brevId = BREV_ID,
                sakId = SAK_ID,
            )
    }
}
