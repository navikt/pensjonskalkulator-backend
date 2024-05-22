package no.nav.pensjon.kalkulator.omstillingsstoenad.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingsstoenadService
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OmstillingsstoenadController::class)
@Import(MockSecurityConfiguration::class)
class OmstillingsstoenadControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: OmstillingsstoenadService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun mottarOmstillingsstoenad() {
        `when`(service.mottarOmstillingsstoenad()).thenReturn(true)

        val response = mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn()

        assertEquals(RESPONSE_BODY_MOTTAR, response.response.contentAsString)
    }

    @Test
    fun `bruker mottar ikke omstillingsstoenad`() {
        `when`(service.mottarOmstillingsstoenad()).thenReturn(false)

        val response = mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn()

        assertEquals(RESPONSE_BODY_MOTTAR_IKKE, response.response.contentAsString)
    }

    private companion object {

        private const val URL = "/api/v1/mottar-omstillingsstoenad"

        @Language("json")
        private const val RESPONSE_BODY_MOTTAR = """{"brukerMottarOmstillingsstoenad":true}"""

        @Language("json")
        private const val RESPONSE_BODY_MOTTAR_IKKE = """{"brukerMottarOmstillingsstoenad":false}"""
    }
}