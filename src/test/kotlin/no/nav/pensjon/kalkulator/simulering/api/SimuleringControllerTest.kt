package no.nav.pensjon.kalkulator.simulering.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.SimulertAlderspensjon
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SimuleringController::class)
@Import(MockSecurityConfiguration::class)
class SimuleringControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var simuleringService: SimuleringService

    @Test
    fun simulerAlderspensjon() {
        `when`(simuleringService.simulerAlderspensjon(anyObject())).thenReturn(simuleringsresultat())

        mvc.perform(
            post(URL)
                .with(csrf())
                .content(REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/alderspensjon/simulering"

        @Language("json")
        private val REQUEST_BODY = """{
            "simuleringstype": "AP",
            "forventetInntekt": 100000,
            "uttaksgrad": 100,
            "foersteUttaksdato": "2031-11-01",
            "sivilstand": "UGIFT",
            "epsHarInntektOver2G": false
        }""".trimIndent()

        @Language("json")
        private val RESPONSE_BODY = """{
            "alderspensjon": [
              {
                "beloep": 215026,
                "alder": 67
              }
            ],
            "afpPrivat": []
        }""".trimIndent()

        private fun simuleringsresultat() = Simuleringsresultat(
            alderspensjon = listOf(SimulertAlderspensjon(alder = 67, beloep = 215026)),
            afpPrivat = emptyList()
        )

        private fun <T> anyObject(): T {
            return Mockito.any()
        }
    }
}
