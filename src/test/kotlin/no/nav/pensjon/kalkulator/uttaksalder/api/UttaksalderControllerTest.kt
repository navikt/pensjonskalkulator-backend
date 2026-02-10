package no.nav.pensjon.kalkulator.uttaksalder.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.ShadowTilgangComparator
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UttaksalderController::class)
@Import(MockSecurityConfiguration::class)
internal class UttaksalderControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var uttaksalderService: UttaksalderService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockkBean(relaxed = true)
    private lateinit var shadowTilgangComparator: ShadowTilgangComparator

    @MockkBean
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
            every { pidExtractor.pid() } returns pid
            every { adresseService.adressebeskyttelseGradering(any()) } returns AdressebeskyttelseGradering.UGRADERT
            every { groupMembershipService.innloggetBrukerHarTilgang(any()) } returns true
            every { auditor.audit(any(), any()) } returns Unit
        }

        should("normalt returnere tidligste hel uttaksalder V3") {
            every { uttaksalderService.finnTidligsteUttaksalder(any()) } returns uttaksalder

            mvc.perform(
                post("/api/v3/tidligste-hel-uttaksalder")
                    .with(csrf())
                    .content(requestBodyV3())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(content().json(responseBody()))
        }
    }

    private companion object {
        private val uttaksalder = Alder(aar = 67, maaneder = 10)

        @Language("json")
        private fun requestBodyV3(
            sivilstand: Sivilstand = Sivilstand.UGIFT,
            harEps: Boolean = true,
            sisteInntekt: Int = 100_000,
            simuleringType: SimuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT
        ): String = """
            {
              "simuleringstype": "${simuleringType.name}",
              "aarligInntektFoerUttakBeloep": $sisteInntekt,
              "aarligInntektVsaPensjon": null,
              "sivilstand": "$sivilstand",
              "epsHarInntektOver2G": $harEps,
              "epsHarPensjon": $harEps,
              "innvilgetLivsvarigOffentligAfp": null,
              "utenlandsperiodeListe": [{
                "fom": "1990-01-02",
                "tom": "1999-11-30",
                "landkode": "AUS",
                "arbeidetUtenlands": true
              }]
            }
        """.trimIndent()

        @Language("json")
        private fun responseBody(aar: Int = uttaksalder.aar, maaned: Int = uttaksalder.maaneder): String = """
            {
                "aar": $aar,
                "maaneder": $maaned
            }
        """.trimIndent()
    }
}
