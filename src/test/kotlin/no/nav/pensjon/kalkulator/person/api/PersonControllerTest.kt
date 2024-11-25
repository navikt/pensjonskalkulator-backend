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
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PersonController::class)
@Import(MockSecurityConfiguration::class)
class PersonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: PersonService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun `personV3 without PID`() {
        `when`(service.getPerson()).thenReturn(skiltPerson())

        mvc.perform(
            post(URL_V3)
                .with(csrf())
                .content("")
        )
            .andExpect(request().attribute("pid", null))
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_V3))
    }

    @Test
    fun `personV3 with PID`() {
        `when`(service.getPerson()).thenReturn(skiltPerson())

        mvc.perform(
            post(URL_V3)
                .with(csrf())
                .content(requestBodyWithPid())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(request().attribute("pid", "12906498357"))
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_V3))
    }

    @Test
    fun `personV4 without PID`() {
        `when`(service.getPerson()).thenReturn(skiltPerson())

        mvc.perform(
            post(URL_V4)
                .with(csrf())
                .content("")
        )
            .andExpect(request().attribute("pid", null))
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_V3))
    }

    @Test
    fun `personV4 with PID`() {
        `when`(service.getPerson()).thenReturn(skiltPerson())

        mvc.perform(
            post(URL_V4)
                .with(csrf())
                .content(requestBodyWithPid())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(request().attribute("pid", "12906498357"))
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_V4))
    }

    private companion object {
        private const val URL_V3 = "/api/v3/person"
        private const val URL_V4 = "/api/v4/person"

        @Language("json")
        private fun requestBodyWithPid() = """{
            "pid": "12906498357"
        }""".trimIndent()

        @Language("json")
        private const val RESPONSE_BODY_V3 = """{
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
