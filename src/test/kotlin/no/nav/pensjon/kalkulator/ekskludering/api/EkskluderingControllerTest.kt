package no.nav.pensjon.kalkulator.ekskludering.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(EkskluderingController::class)
@Import(MockSecurityConfiguration::class)
class EkskluderingControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var service: EkskluderingFacade

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var groupMembershipService: GroupMembershipService

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

        context("erEkskludertV1") {
            should("normalt returnere status 'OK' og JSON-respons`") {
                every {
                    service.ekskluderingPgaSakEllerApoteker()
                } returns EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)

                mvc.perform(
                    get(EKSKLUDERT_URL_V1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk())
                    .andExpect(content().json(APOTEKER_EKSKLUDERT_RESPONSE_BODY))
            }

            should("gi 'ekskludert' for brukere med løpende ufoeretrygd") {
                every {
                    service.ekskluderingPgaSakEllerApoteker()
                } returns EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD)

                mvc.perform(
                    get(EKSKLUDERT_URL_V1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk())
                    .andExpect(content().json(EKSKLUDERT_PGA_UFOERETRYGD_RESPONSE_BODY))
            }
        }

        context("erEkskludertV2") {
            should("gi 'ekskludert' for medlemmer av apotekerforeningen") {
                every { service.apotekerEkskludering() } returns
                        EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)

                mvc.perform(
                    get(EKSKLUDERT_URL_V2)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk())
                    .andExpect(content().json(APOTEKER_EKSKLUDERT_RESPONSE_BODY))
            }

            should("gi 'ikke ekskludert' hvis ikke ekskludert som apoteker") {
                every {
                    service.apotekerEkskludering()
                } returns EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)

                mvc.perform(
                    get(EKSKLUDERT_URL_V2)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk())
                    .andExpect(content().json(IKKE_EKSKLUDERT_RESPONSE_BODY))
            }
        }

        context("er apoteker V1") {
            should("gi 'er apoteker' for medlemmer av apotekerforeningen") {
                every {
                    service.apotekerEkskludering()
                } returns EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)

                mvc.perform(
                    get(APOTEKER_URL_V1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk())
                    .andExpect(content().json(APOTEKER_RESPONSE_BODY))
            }

            should("gi 'ikke apoteker' hvis ikke ekskludert som apoteker") {
                every {
                    service.apotekerEkskludering()
                } returns EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)

                mvc.perform(
                    get(APOTEKER_URL_V1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk())
                    .andExpect(content().json(IKKE_APOTEKER_RESPONSE_BODY))
            }
        }
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
