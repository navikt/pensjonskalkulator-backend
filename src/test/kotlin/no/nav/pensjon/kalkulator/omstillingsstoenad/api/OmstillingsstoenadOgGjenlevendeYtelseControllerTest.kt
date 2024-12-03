package no.nav.pensjon.kalkulator.omstillingsstoenad.api

import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseService
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(OmstillingsstoenadOgGjenlevendeYtelseController::class)
@Import(MockSecurityConfiguration::class)
class OmstillingsstoenadOgGjenlevendeYtelseControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var service: OmstillingOgGjenlevendeYtelseService

    @MockitoBean
    private lateinit var traceAid: TraceAid

    @MockitoBean
    private lateinit var pidExtractor: PidExtractor

    @MockitoBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockitoBean
    private lateinit var auditor: Auditor

    @Test
    fun `bruker mottar enten omstillingsstoenad eller gjenlevende ytelse`() = runTest {
        `when`(service.harLoependeSaker()).thenReturn(true)

        val res = mvc.get(URL).asyncDispatch()
            .andExpect { status().isOk() }
            .andReturn()

        assertEquals(RESPONSE_BODY_MOTTAR, res.response.contentAsString)
    }

    @Test
    fun `bruker mottar verken omstillingsstoenad eller gjenlevende ytelsee`() = runTest {
        `when`(service.harLoependeSaker()).thenReturn(false)

        val res = mvc.get(URL).asyncDispatch()
            .andExpect { status().isOk() }
            .andReturn()

        assertEquals(RESPONSE_BODY_MOTTAR_IKKE, res.response.contentAsString)
    }

    private companion object {
        private const val URL = "/api/v1/loepende-omstillingsstoenad-eller-gjenlevendeytelse"

        @Language("json")
        private const val RESPONSE_BODY_MOTTAR = """{"harLoependeSak":true}"""

        @Language("json")
        private const val RESPONSE_BODY_MOTTAR_IKKE = """{"harLoependeSak":false}"""
    }
}
