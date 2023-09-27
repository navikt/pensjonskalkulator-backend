package no.nav.pensjon.kalkulator.tech.toggle.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(FeatureToggleController::class)
@Import(MockSecurityConfiguration::class)
class FeatureToggleControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: FeatureToggleService

    @MockBean
    private lateinit var traceAid: TraceAid

    @Test
    fun isEnabled() {
        `when`(service.isEnabled("feature1")).thenReturn(true)

        mvc.perform(MockMvcRequestBuilders.get(URL))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/feature/feature1"

        private const val RESPONSE_BODY = """{
        "enabled": true
    }"""
    }
}
