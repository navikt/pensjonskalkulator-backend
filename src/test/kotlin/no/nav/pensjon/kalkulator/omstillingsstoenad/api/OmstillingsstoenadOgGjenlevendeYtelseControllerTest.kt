package no.nav.pensjon.kalkulator.omstillingsstoenad.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseService
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OmstillingsstoenadOgGjenlevendeYtelseController::class)
@Import(MockSecurityConfiguration::class)
class OmstillingsstoenadOgGjenlevendeYtelseControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var service: OmstillingOgGjenlevendeYtelseService

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

        test("bruker mottar enten omstillingsstønad eller gjenlevendeytelse") {
            coEvery { service.harLoependeSaker() } returns true

            val res = mvc.get(URL).asyncDispatch()
                .andExpect { status().isOk() }
                .andReturn()

            assertEquals(RESPONSE_BODY_MOTTAR, res.response.contentAsString)
        }

        test("bruker mottar verken omstillingsstønad eller gjenlevendeytelse") {
            coEvery { service.harLoependeSaker() } returns false

            val res = mvc.get(URL).asyncDispatch()
                .andExpect { status().isOk() }
                .andReturn()

            assertEquals(RESPONSE_BODY_MOTTAR_IKKE, res.response.contentAsString)
        }
    }

    private companion object {
        private const val URL = "/api/v1/loepende-omstillingsstoenad-eller-gjenlevendeytelse"

        @Language("json")
        private const val RESPONSE_BODY_MOTTAR = """{"harLoependeSak":true}"""

        @Language("json")
        private const val RESPONSE_BODY_MOTTAR_IKKE = """{"harLoependeSak":false}"""
    }
}
