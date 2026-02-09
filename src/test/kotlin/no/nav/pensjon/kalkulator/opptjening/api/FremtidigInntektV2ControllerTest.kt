package no.nav.pensjon.kalkulator.opptjening.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.ShadowTilgangComparator
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(InntektController::class)
@Import(MockSecurityConfiguration::class)
class FremtidigInntektV2ControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var service: InntektService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockkBean(relaxed = true)
    private lateinit var shadowTilgangComparator: ShadowTilgangComparator

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

        should("normalt returnere inntektsforhold") {
            val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2021, BigDecimal("123000"))
            every { service.sistePensjonsgivendeInntekt() } returns inntekt

            mvc.perform(
                get(URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY))
        }
    }

    private companion object {
        private const val URL = "/api/inntekt"

        @Language("json")
        private const val RESPONSE_BODY = """{
	"beloep": 123000,
	"aar": 2021
}"""
    }
}
