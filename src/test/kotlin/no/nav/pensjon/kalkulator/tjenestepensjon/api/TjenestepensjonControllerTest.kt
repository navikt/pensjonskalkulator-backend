package no.nav.pensjon.kalkulator.tjenestepensjon.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.MaanedligBeloep
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
        val fom =  LocalDate.of(2025, 1, 1)
        `when`(tjenestepensjonService.hentAfpOffentligLivsvarigDetaljer())
            .thenReturn(
                AfpOffentligLivsvarigResult(
                    afpStatus = true,
                    virkningFom = fom,
                    maanedligBeloepListe = listOf(MaanedligBeloep(fom, 15000)),
                    sistBenyttetGrunnbeloep = 123000
                )
            )

        mvc.perform(
            get(URL_AFP_OFFENTLIG_LIVSVARIG_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_AFP_OFFENTLIG_LIVSVARIG_V2))
    }

    @Test
    fun hentAfpOffentligLivsvarigDetaljerV3() {
        val fom =  LocalDate.of(2025, 10, 1)
        `when`(tjenestepensjonService.hentAfpOffentligLivsvarigDetaljer())
            .thenReturn(
                AfpOffentligLivsvarigResult(
                    afpStatus = true,
                    virkningFom = fom,
                    maanedligBeloepListe = listOf(
                        MaanedligBeloep(fom, 15000),
                        MaanedligBeloep(LocalDate.of(2026,1,1), 16000),
                        ),
                    sistBenyttetGrunnbeloep = 123000
                )
            )

        mvc.perform(
            get(URL_AFP_OFFENTLIG_LIVSVARIG_V3)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_AFP_OFFENTLIG_LIVSVARIG_V3))
    }

    private companion object {

        private const val URL = "/api/tpo-medlemskap"
        private const val URL_V1 = "/api/v1/tpo-medlemskap"
        private const val URL_AFP_OFFENTLIG_LIVSVARIG_V2 = "/api/v2/tpo-livsvarig-offentlig-afp"
        private const val URL_AFP_OFFENTLIG_LIVSVARIG_V3 = "/api/v3/tpo-livsvarig-offentlig-afp"

        @Language("json")
        private const val RESPONSE_BODY = """{
	"harTjenestepensjonsforhold": true
}"""

        @Language("json")
        private const val RESPONSE_BODY_V1 = """{
        "tpLeverandoerListe": ["Maritim pensjonskasse", "Statens pensjonskasse", "Kommunal Landspensjonskasse"]
    }"""

        @Language("json")
        private const val RESPONSE_BODY_AFP_OFFENTLIG_LIVSVARIG_V2 = """{
        "afpStatus": true,
        "maanedligBeloep": 15000
    }"""

        @Language("json")
        private const val RESPONSE_BODY_AFP_OFFENTLIG_LIVSVARIG_V3 = """{
        "afpStatus": true,
        "maanedligBeloepListe": [
            {
            "virkningFom":"2025-10-01",
            "beloep":15000
            },
            {
            "virkningFom":"2026-01-01",
            "beloep":16000
            }
        ],
        "virkningFom":"2025-10-01",
        "sistBenyttetGrunnbeloep":123000
    }"""
    }
}
