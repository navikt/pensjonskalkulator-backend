package no.nav.pensjon.kalkulator.simulering.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.simulering.*
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
    fun `simulerer alderspensjon`() {
        `when`(simuleringService.simulerAlderspensjon(anyObject())).thenReturn(simuleringsresultat(SimuleringType.ALDERSPENSJON))

        mvc.perform(
            post(URL)
                .with(csrf())
                .content(requestBody(SimuleringType.ALDERSPENSJON))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBody(SimuleringType.ALDERSPENSJON)))
    }

    @Test
    fun `simulerer alderspensjon med afp privat`() {
        `when`(simuleringService.simulerAlderspensjon(anyObject())).thenReturn(simuleringsresultat(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))

        mvc.perform(
            post(URL)
                .with(csrf())
                .content(requestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)))
    }

    private companion object {

        private const val URL = "/api/alderspensjon/simulering"
        private const val PENSJONSBELOEP = 123456

        @Language("json")
        private fun requestBody(simuleringstype: SimuleringType) = """{
            "simuleringstype": "$simuleringstype",
            "forventetInntekt": 100000,
            "uttaksgrad": 100,
            "foersteUttaksalder": { "aar": 67, "maaned": 1 },
            "foedselsdato": "1963-12-31",
            "sivilstand": "UGIFT",
            "epsHarInntektOver2G": false
        }""".trimIndent()

        @Language("json")
        private fun responseBody(simuleringstype: SimuleringType) = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "afpPrivat": ${
            when (simuleringstype) {
                SimuleringType.ALDERSPENSJON -> "[]"
                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT -> """
              [
                {
                  "beloep": 22056,
                  "alder": 67
                }
              ]
              """
            }
        }
        }""".trimIndent()

        private fun simuleringsresultat(simuleringType: SimuleringType) =
            when (simuleringType) {
                SimuleringType.ALDERSPENSJON -> Simuleringsresultat(
                    alderspensjon = listOf(SimulertAlderspensjon(alder = 67, beloep = PENSJONSBELOEP)),
                    afpPrivat = emptyList()
                )

                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT -> Simuleringsresultat(
                    alderspensjon = listOf(SimulertAlderspensjon(alder = 67, beloep = PENSJONSBELOEP)),
                    afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 22056)),
                )
            }


        private fun <T> anyObject(): T {
            return Mockito.any()
        }
    }
}
