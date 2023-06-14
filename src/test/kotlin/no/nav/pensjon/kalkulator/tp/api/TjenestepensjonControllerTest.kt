package no.nav.pensjon.kalkulator.tp.api

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tp.TjenestepensjonService
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

@WebMvcTest(TjenestepensjonController::class)
@Import(MockSecurityConfiguration::class)
class TjenestepensjonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: TjenestepensjonService

    @Test
    fun fetchTjenestepensjonsforhold() {
        `when`(service.harTjenestepensjonsforhold()).thenReturn(true)

        mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/tpo-medlemskap"

        @Language("json")
        private const val RESPONSE_BODY = """{
	"harTjenestepensjonsforhold": true
}"""
    }
}
