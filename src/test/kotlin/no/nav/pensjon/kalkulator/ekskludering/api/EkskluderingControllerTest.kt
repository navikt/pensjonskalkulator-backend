package no.nav.pensjon.kalkulator.ekskludering.api

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(EkskluderingController::class)
@Import(MockSecurityConfiguration::class)
class EkskluderingControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: EkskluderingFacade

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun `'erEkskludertV1' returnerer normalt status 'OK' og JSON-respons`() {
        val ekskluderingStatus = EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
        `when`(service.erEkskludert()).thenReturn(ekskluderingStatus)

        mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_APOTEKER))
    }

    @Test
    fun `'erEkskludertV1' returnerer ekskludert for brukere med loepende ufoeretrygd`() {
        val ekskluderingStatus = EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD)
        `when`(service.erEkskludert()).thenReturn(ekskluderingStatus)

        mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_HAR_LOEPENDE_UFOERETRYGD))
    }

    @Test
    fun `'erEkskludertV2' returnerer ekskludert for medlemmer av apotekerforeningen`() {
        val ekskluderingStatus = EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
        `when`(service.erEkskludertV2()).thenReturn(ekskluderingStatus)

        mvc.perform(
            get(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_APOTEKER))
    }

    @Test
    fun `'erEkskludertV2' returnerer ikke ekskludert`() {
        val ekskluderingStatus = EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)
        `when`(service.erEkskludertV2()).thenReturn(ekskluderingStatus)

        mvc.perform(
            get(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_IKKE_EKSKLUDERT))
    }

    private companion object {

        private const val URL = "/api/v1/ekskludert"
        private const val URL_V2 = "/api/v2/ekskludert"

        @Language("json")
        private const val RESPONSE_BODY_APOTEKER = """{
	"ekskludert": true,
	"aarsak": "ER_APOTEKER"
}"""
        @Language("json")
        private const val RESPONSE_BODY_HAR_LOEPENDE_UFOERETRYGD = """{
	"ekskludert": true,
	"aarsak": "HAR_LOEPENDE_UFOERETRYGD"
}"""

        @Language("json")
        private const val RESPONSE_BODY_IKKE_EKSKLUDERT = """{
	"ekskludert": false,
	"aarsak": "NONE"
}"""
    }
}
