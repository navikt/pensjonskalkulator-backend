package no.nav.pensjon.kalkulator.ufoere.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.DateFactory
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.TilgangService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.ufoere.Ufoeregrad
import no.nav.pensjon.kalkulator.ufoere.UfoerepensjonService
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UfoerepensjonController::class)
@Import(MockSecurityConfiguration::class)
class UfoerepensjonControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var ufoeretrygdService: UfoerepensjonService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockkBean
    private lateinit var tilgangService: TilgangService

    @MockkBean
    private lateinit var navIdExtractor: SecurityContextNavIdExtractor

    @MockkBean
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
            every { pidExtractor.pid() } returns pid
            every { adresseService.adressebeskyttelseGradering(any()) } returns AdressebeskyttelseGradering.UGRADERT
            every { groupMembershipService.innloggetBrukerHarTilgang(any()) } returns true
            every { auditor.audit(any(), any()) } returns Unit
        }

        context("harLoependeUfoerepensjon") {
            should("normalt returnere status OK og JSON-respons") {
                every { ufoeretrygdService.harLoependeUfoerepensjon(DateFactory.date) } returns true

                mvc.perform(
                    MockMvcRequestBuilders.post(URL_UFOEREPENSJON)
                        .with(csrf())
                        .content(requestBody())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(content().json(RESPONSE_BODY))
            }
        }

        context("hentUfoeregrad") {
            should("normalt returnere status OK og JSON-respons") {
                every { ufoeretrygdService.hentUfoeregrad() } returns Ufoeregrad(50)

                mvc.perform(
                    MockMvcRequestBuilders.get(URL_HENT_UFOEREGRAD)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(content().json("""{"ufoeregrad": 50}"""))
            }
        }
    }

    private companion object {
        private const val URL_UFOEREPENSJON = "/api/ufoerepensjon"
        private const val URL_HENT_UFOEREGRAD = "/api/v1/ufoeregrad"

        @Language("json")
        private fun requestBody() = """
            {
              "fom": "2023-04-05"
            }
        """.trimIndent()

        @Language("json")
        private const val RESPONSE_BODY = """{
	"harUfoerepensjon": true
}"""
    }
}
