package no.nav.pensjon.kalkulator.ekskludering.api

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(EkskluderingController::class)
@Import(MockSecurityConfiguration::class)
class EkskluderingControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: EkskluderingFacade

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun `'erEkskludert' returnerer normalt status 'OK' og JSON-respons`() {
        val ekskluderingStatus = EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
        `when`(service.erEkskludert()).thenReturn(ekskluderingStatus)

        mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/v1/ekskludert"

        @Language("json")
        private const val RESPONSE_BODY = """{
	"ekskludert": true,
	"aarsak": "ER_APOTEKER"
}"""
    }
}
