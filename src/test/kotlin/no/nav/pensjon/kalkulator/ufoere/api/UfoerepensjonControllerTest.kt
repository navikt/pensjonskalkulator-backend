package no.nav.pensjon.kalkulator.ufoere.api

import no.nav.pensjon.kalkulator.mock.DateFactory
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.ufoere.Ufoeregrad
import no.nav.pensjon.kalkulator.ufoere.UfoerepensjonService
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UfoerepensjonController::class)
@Import(MockSecurityConfiguration::class)
class UfoerepensjonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var ufoeretrygdService: UfoerepensjonService

    @MockitoBean
    private lateinit var traceAid: TraceAid

    @MockitoBean
    private lateinit var pidExtractor: PidExtractor

    @MockitoBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockitoBean
    private lateinit var auditor: Auditor

    @Test
    fun harLoependeUfoerepensjon() {
        `when`(ufoeretrygdService.harLoependeUfoerepensjon(DateFactory.date)).thenReturn(true)

        mvc.perform(
            MockMvcRequestBuilders.post(URL_UFOEREPENSJON)
                .with(csrf())
                .content(requestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(RESPONSE_BODY))
    }

    @Test
    fun hentUfoeregrad() {
        `when`(ufoeretrygdService.hentUfoeregrad()).thenReturn(Ufoeregrad(50))

        mvc.perform(
            MockMvcRequestBuilders.get(URL_HENT_UFOEREGRAD)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json("""{"ufoeregrad": 50}"""))
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
