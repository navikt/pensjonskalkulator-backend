package no.nav.pensjon.kalkulator.uttaksalder.api

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.UtenlandsperiodeSpec
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

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
    fun `finnTidligsteHelUttaksalderV1 returns response`() {
        val spec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.UGIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
            heltUttak = null,
            utenlandsperiodeListe = listOf(
                UtenlandsperiodeSpec(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidetUtenlands = true
                )
            )
        )
        `when`(uttaksalderService.finnTidligsteUttaksalder(spec)).thenReturn(uttaksalder)

        mvc.perform(
            post("/api/v1/tidligste-hel-uttaksalder")
                .with(csrf())
                .content(requestBodyV1())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(responseBodyV1()))
    }

    private companion object {
        private val uttaksalder = Alder(aar = 67, maaneder = 10)

        @Language("json")
        private fun requestBodyV1(
            sivilstand: Sivilstand = Sivilstand.UGIFT,
            harEps: Boolean = true,
            sisteInntekt: Int = 100_000,
            simuleringType: SimuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT
        ): String = """
            {
              "simuleringstype": "${simuleringType.name}",
              "sivilstand": "$sivilstand",
              "harEps": $harEps,
              "aarligInntektFoerUttakBeloep": $sisteInntekt,
              "utenlandsperiodeListe": [{
                "fom": "1990-01-02",
                "tom": "1999-11-30",
                "landkode": "AUS",
                "arbeidetUtenlands": true
              }]
            }
        """.trimIndent()

        @Language("json")
        private fun responseBodyV1(aar: Int = uttaksalder.aar, maaned: Int = uttaksalder.maaneder): String = """
            {
                "aar": $aar,
                "maaneder": $maaned
            }
        """.trimIndent()
    }
}
