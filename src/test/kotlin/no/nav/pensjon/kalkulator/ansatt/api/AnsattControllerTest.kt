package no.nav.pensjon.kalkulator.ansatt.api

import no.nav.pensjon.kalkulator.ansatt.AnsattService
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AnsattController::class)
@Import(MockSecurityConfiguration::class)
class AnsattControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var service: AnsattService

    @MockitoBean
    private lateinit var traceAid: TraceAid

    @MockitoBean
    private lateinit var pidExtractor: PidExtractor

    @MockitoBean
    private lateinit var fortroligAdresseService: FortroligAdresseService

    @MockitoBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockitoBean
    private lateinit var auditor: Auditor

    @Test
    fun ansattId() {
        `when`(service.getAnsattId()).thenReturn("id1")

        mvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {
        private const val URL = "/api/v1/ansatt-id"

        private const val RESPONSE_BODY = """{
        "id": "id1"
    }"""
    }
}
