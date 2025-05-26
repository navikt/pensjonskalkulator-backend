package no.nav.pensjon.kalkulator.aldersgrense.api

import no.nav.pensjon.kalkulator.aldersgrense.AldersgrenseService
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseSpec
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AldersgrenseController::class)
@Import(MockSecurityConfiguration::class)
class AldersgrenseControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var service: AldersgrenseService

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
    fun `test 'aldersgrense' endpoint version 1 with birth year 1963`() {
        val spec = AldersgrenseSpec(foedselsdato = 1963)
        val pensjoneringAldre = PensjoneringAldre(
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAldersgrense = Alder(aar = 62, maaneder = 0)
        )

        `when`(service.hentAldersgrenser(spec)).thenReturn(pensjoneringAldre)

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .content(REQUEST_BODY_1963)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_1963))
    }

    @Test
    fun `test 'aldersgrense' endpoint version 1 with birth year 1970`() {
        val spec = AldersgrenseSpec(foedselsdato = 1970)
        val pensjoneringAldre = PensjoneringAldre(
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAldersgrense = Alder(aar = 62, maaneder = 0)
        )

        `when`(service.hentAldersgrenser(spec)).thenReturn(pensjoneringAldre)

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .content(REQUEST_BODY_1970)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_1970))
    }

    private companion object {
        private const val URL_V1 = "/api/v1/aldersgrense"

        @Language("json")
        private const val REQUEST_BODY_1963 = """{
            "foedselsdato": 1963
        }"""

        @Language("json")
        private const val REQUEST_BODY_1970 = """{
            "foedselsdato": 1970
        }"""

        @Language("json")
        private const val RESPONSE_BODY_1963 = """{
            "normertPensjoneringsalder": {
                "aar": 62,
                "maaneder": 0
            },
            "nedreAldersgrense": {
                "aar": 62,
                "maaneder": 0
            }
        }"""

        @Language("json")
        private const val RESPONSE_BODY_1970 = """{
            "normertPensjoneringsalder": {
                "aar": 62,
                "maaneder": 0
            },
            "nedreAldersgrense": {
                "aar": 62,
                "maaneder": 0
            }
        }"""
    }
}