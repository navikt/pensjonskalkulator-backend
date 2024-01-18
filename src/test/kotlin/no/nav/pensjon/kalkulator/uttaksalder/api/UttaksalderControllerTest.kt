package no.nav.pensjon.kalkulator.uttaksalder.api

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UttaksalderController::class)
@Import(MockSecurityConfiguration::class)
internal class UttaksalderControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var uttaksalderService: UttaksalderService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun `finnTidligsteUttaksalder version 1`() {
        val spec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.UGIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
            heltUttak = HeltUttak(Alder(0, 0), null)
        )
        `when`(uttaksalderService.finnTidligsteUttaksalder(spec)).thenReturn(uttaksalder)

        mvc.perform(
            post("/api/v1/tidligste-uttaksalder")
                .with(csrf())
                .content(requestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(responseBodyV1()))
    }

    @Test
    fun `finnTidligsteUttaksalder version 2`() {
        val spec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.UGIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
            heltUttak = HeltUttak(
                Alder(67, 0),
                Inntekt(1000, Alder(70, 11))
            )
        )
        `when`(uttaksalderService.finnTidligsteUttaksalder(spec)).thenReturn(uttaksalder)

        mvc.perform(
            post("/api/v2/tidligste-uttaksalder")
                .with(csrf())
                .content(requestBodyV2())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(responseBodyV2()))
    }

    @Language("json")
    private fun requestBody(
        sivilstand: Sivilstand = Sivilstand.UGIFT,
        harEps: Boolean = true,
        sisteInntekt: Int = 100_000,
        simuleringType: SimuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT
    ): String = """
            {
              "sivilstand": "$sivilstand",
              "harEps": $harEps,
              "sisteInntekt": $sisteInntekt,
              "simuleringstype": "${simuleringType.name}"
            }
        """.trimIndent()

    @Language("json")
    private fun responseBodyV1(aar: Int = uttaksalder.aar, maaned: Int = uttaksalder.maaneder): String = """
            {
                "aar": $aar,
                "maaneder": $maaned
            }
        """.trimIndent()

    private companion object {
        private val uttaksalder = Alder(67, 10)

        @Language("json")
        private fun requestBodyV2(
            sivilstand: Sivilstand = Sivilstand.UGIFT,
            harEps: Boolean = true,
            sisteInntekt: Int = 100_000,
            simuleringType: SimuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT
        ): String = """
            {
              "simuleringstype": "${simuleringType.name}",
              "sivilstand": "$sivilstand",
              "harEps": $harEps,
              "aarligInntekt": $sisteInntekt,
              "heltUttak": {
                "uttaksalder": {
                  "aar": 67,
                  "maaneder": 0
                },
                "aarligInntektVsaPensjon": {
                  "beloep": 1000,
                  "sluttAlder": {
                    "aar": 70,
                    "maaneder": 11
                  }
                }
              }
            }
        """.trimIndent()

        @Language("json")
        private fun responseBodyV2(aar: Int = uttaksalder.aar, maaned: Int = uttaksalder.maaneder): String = """
            {
                "aar": $aar,
                "maaneder": $maaned
            }
        """.trimIndent()
    }
}
