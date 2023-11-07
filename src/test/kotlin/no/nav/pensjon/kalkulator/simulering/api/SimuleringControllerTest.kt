package no.nav.pensjon.kalkulator.simulering.api

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(SimuleringController::class)
@Import(MockSecurityConfiguration::class)
class SimuleringControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var simuleringService: SimuleringService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun `simulerer alderspensjon`() {
        val spec = impersonalSpec(SimuleringType.ALDERSPENSJON)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

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
    fun `simulerer alderspensjon med AFP privat`() {
        val spec = impersonalSpec(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL)
                .with(csrf())
                .content(requestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)))
    }

    @Test
    fun `simulering responds 'vilkaar ikke oppfylt' when Conflict`() {
        val spec = impersonalSpec(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenThrow(EgressException("", statusCode = HttpStatus.CONFLICT))

        mvc.perform(
            post(URL)
                .with(csrf())
                .content(requestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(SimuleringController.VILKAAR_IKKE_OPPFYLT_EXAMPLE))
    }

    private companion object {

        private const val URL = "/api/v1/alderspensjon/simulering"
        private const val PENSJONSBELOEP = 123456

        @Language("json")
        private fun requestBody(simuleringType: SimuleringType) = """{
            "simuleringstype": "$simuleringType",
            "forventetInntekt": 100000,
            "uttaksgrad": 100,
            "foersteUttaksalder": { "aar": 67, "maaneder": 1 },
            "foedselsdato": "1963-12-31",
            "sivilstand": "UGIFT",
            "epsHarInntektOver2G": false
        }""".trimIndent()

        private fun impersonalSpec(simuleringType: SimuleringType) =
            ImpersonalSimuleringSpec(
                simuleringType = simuleringType,
                uttaksgrad = Uttaksgrad.HUNDRE_PROSENT,
                foersteUttaksalder = Alder(67, 1),
                foedselsdato = LocalDate.of(1963, 12, 31),
                epsHarInntektOver2G = false,
                forventetInntekt = 100_000,
                sivilstand = Sivilstand.UGIFT
            )

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
    }
}
