package no.nav.pensjon.kalkulator.tjenestepensjon.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(TjenestepensjonController::class)
@Import(MockSecurityConfiguration::class)
class TjenestepensjonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var tjenestepensjonService: TjenestepensjonService

    @MockitoBean
    private lateinit var traceAid: TraceAid

    @MockitoBean
    private lateinit var pidExtractor: PidExtractor

    @MockitoBean
    private lateinit var fortroligAdresseService: FortroligAdresseService

    @MockitoBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockitoBean
    private lateinit var auditor: Auditor

    @Test
    fun fetchTjenestepensjonsforhold() {
        `when`(tjenestepensjonService.harTjenestepensjonsforhold()).thenReturn(true)

        mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    @Test
    fun hentMedlemskapITjenestepensjonsordninger() {
        `when`(tjenestepensjonService.hentMedlemskapITjenestepensjonsordninger()).thenReturn(
            listOf(
                "Maritim pensjonskasse",
                "Statens pensjonskasse",
                "Kommunal Landspensjonskasse"
            )
        )

        mvc.perform(
            get(URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_V1))
    }

    @Test
    fun hentAfpOffentligLivsvarigDetaljer() {
        `when`(tjenestepensjonService.hentAfpOffentligLivsvarigDetaljer())
            .thenReturn(
                AfpOffentligLivsvarigResult(
                    afpStatus = true,
                    virkningFom = LocalDate.of(2025, 1, 1),
                    maanedligBeloep = 15000,
                    sistBenyttetGrunnbeloep = 123000
                )
            )

        mvc.perform(
            get(URL_AFP_OFFENTLIG_LIVSVARIG)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_AFP_OFFENTLIG_LIVSVARIG))
    }

    private companion object {

        private const val URL = "/api/tpo-medlemskap"
        private const val URL_V1 = "/api/v1/tpo-medlemskap"
        private const val URL_AFP_OFFENTLIG_LIVSVARIG = "/api/v1/tpo-afp-offentlig-livsvarig"

        @Language("json")
        private const val RESPONSE_BODY = """{
	"harTjenestepensjonsforhold": true
}"""

        @Language("json")
        private const val RESPONSE_BODY_V1 = """{
        "tpLeverandoerListe": ["Maritim pensjonskasse", "Statens pensjonskasse", "Kommunal Landspensjonskasse"]
    }"""

        @Language("json")
        private const val RESPONSE_BODY_AFP_OFFENTLIG_LIVSVARIG = """{
        "afpStatus": true,
        "maanedligBeloep": 15000
    }"""
    }
}
