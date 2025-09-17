package no.nav.pensjon.kalkulator.ekskludering.api

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(EkskluderingController::class)
@Import(MockSecurityConfiguration::class)
class EkskluderingControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var service: EkskluderingFacade

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
    fun `'erEkskludertV1' returnerer normalt status 'OK' og JSON-respons`() {
        `when`(service.ekskluderingPgaSakEllerApoteker())
            .thenReturn(EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER))

        mvc.perform(
            get(EKSKLUDERT_URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(APOTEKER_EKSKLUDERT_RESPONSE_BODY))
    }

    @Test
    fun `'erEkskludertV1' skal gi 'ekskludert' for brukere med løpende ufoeretrygd`() {
        `when`(service.ekskluderingPgaSakEllerApoteker())
            .thenReturn(EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD))

        mvc.perform(
            get(EKSKLUDERT_URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(EKSKLUDERT_PGA_UFOERETRYGD_RESPONSE_BODY))
    }

    @Test
    fun `'erEkskludertV2' skal gi 'ekskludert' for medlemmer av apotekerforeningen`() {
        `when`(service.apotekerEkskludering())
            .thenReturn(EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER))

        mvc.perform(
            get(EKSKLUDERT_URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(APOTEKER_EKSKLUDERT_RESPONSE_BODY))
    }

    @Test
    fun `'erEkskludertV2' skal gi 'ikke ekskludert' hvis ikke ekskludert som apoteker`() {
        `when`(service.apotekerEkskludering())
            .thenReturn(EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE))

        mvc.perform(
            get(EKSKLUDERT_URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(IKKE_EKSKLUDERT_RESPONSE_BODY))
    }

    @Test
    fun `'er-apoteker V1' skal gi 'er apoteker' for medlemmer av apotekerforeningen`() {
        `when`(service.apotekerEkskludering())
            .thenReturn(EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER))

        mvc.perform(
            get(APOTEKER_URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(APOTEKER_RESPONSE_BODY))
    }

    @Test
    fun `'er-apoteker V1' skal gi 'ikke apoteker' hvis ikke ekskludert som apoteker`() {
        `when`(service.apotekerEkskludering())
            .thenReturn(EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE))

        mvc.perform(
            get(APOTEKER_URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(IKKE_APOTEKER_RESPONSE_BODY))
    }

    private companion object {

        private const val EKSKLUDERT_URL_V1 = "/api/v1/ekskludert"
        private const val EKSKLUDERT_URL_V2 = "/api/v2/ekskludert"
        private const val APOTEKER_URL_V1 = "/api/v1/er-apoteker"

        @Language("json")
        private const val APOTEKER_RESPONSE_BODY = """{
	"apoteker": true,
	"aarsak": "ER_APOTEKER"
}"""

        @Language("json")
        private const val IKKE_APOTEKER_RESPONSE_BODY = """{
	"apoteker": false,
	"aarsak": "NONE"
}"""

        @Language("json")
        private const val APOTEKER_EKSKLUDERT_RESPONSE_BODY = """{
	"ekskludert": true,
	"aarsak": "ER_APOTEKER"
}"""

        @Language("json")
        private const val EKSKLUDERT_PGA_UFOERETRYGD_RESPONSE_BODY = """{
	"ekskludert": true,
	"aarsak": "HAR_LOEPENDE_UFOERETRYGD"
}"""

        @Language("json")
        private const val IKKE_EKSKLUDERT_RESPONSE_BODY = """{
	"ekskludert": false,
	"aarsak": "NONE"
}"""
    }
}
