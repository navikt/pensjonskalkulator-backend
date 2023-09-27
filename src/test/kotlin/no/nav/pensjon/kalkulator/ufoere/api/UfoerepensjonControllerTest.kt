package no.nav.pensjon.kalkulator.ufoere.api

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.mock.DateFactory
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.ufoere.UfoerepensjonService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UfoerepensjonController::class)
@Import(MockSecurityConfiguration::class)
class UfoerepensjonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: UfoerepensjonService

    @MockBean
    private lateinit var traceAid: TraceAid

    @Test
    fun harLoependeUfoerepensjon() {
        `when`(service.harLoependeUfoerepensjon(DateFactory.date)).thenReturn(true)

        mvc.perform(
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .content(requestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {
        private const val URL = "/api/ufoerepensjon"

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
