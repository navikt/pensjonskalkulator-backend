package no.nav.pensjon.kalkulator.person.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.skiltPerson
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PersonController::class)
@Import(MockSecurityConfiguration::class)
class PersonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var service: PersonService

    @MockitoBean
    private lateinit var traceAid: TraceAid

    @MockitoBean
    private lateinit var pidExtractor: PidExtractor

    @MockitoBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockitoBean
    private lateinit var auditor: Auditor

    @Test
    fun `test 'person' endpoint version 2`() {
        `when`(service.getPerson()).thenReturn(skiltPerson())

        mvc.perform(
            get(URL_V2)
                .with(csrf())
                .content("")
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_V2))
    }

    @Test
    fun `test 'person' endpoint version 4`() {
        `when`(service.getPerson()).thenReturn(skiltPerson())

        mvc.perform(
            get(URL_V4)
                .with(csrf())
                .content("")
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_V4))
    }

    private companion object {
        private const val URL_V2 = "/api/v2/person"
        private const val URL_V4 = "/api/v4/person"

        @Language("json")
        private const val RESPONSE_BODY_V2 = """{
    "navn": "Fornavn1",
    "foedselsdato": "1963-12-31",
    "sivilstand": "SKILT"
}"""

        @Language("json")
        private const val RESPONSE_BODY_V4 = """{
    "navn": "Fornavn1",
    "foedselsdato": "1963-12-31",
    "sivilstand": "SKILT",
    "pensjoneringAldre": {
        "normertPensjoneringsalder": {
            "aar": 67,
            "maaneder": 0
        },
        "nedreAldersgrense": {
            "aar": 62,
            "maaneder": 0
        }
    }
}"""
    }
}
