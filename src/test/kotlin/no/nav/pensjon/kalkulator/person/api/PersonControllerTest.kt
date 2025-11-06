package no.nav.pensjon.kalkulator.person.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.personWithPensjoneringAldre
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.PersonFactory.skiltPerson
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PersonController::class)
@Import(MockSecurityConfiguration::class)
class PersonControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var personService: PersonService

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

        test("'person' endpoint version 2") {
            every { personService.getPerson() } returns skiltPerson()

            mvc.perform(
                get(URL_V2)
                    .with(csrf())
                    .content("")
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_V2))
        }

        test("'person' endpoint version 4") {
            every { personService.getPerson() } returns skiltPerson()

            mvc.perform(
                get(URL_V4)
                    .with(csrf())
                    .content("")
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_V4))
        }

        test("'person' endpoint version 5") {
            every { personService.getPerson() } returns personWithPensjoneringAldre()

            mvc.perform(
                get(URL_V5)
                    .with(csrf())
                    .content("")
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_V5))
        }
    }

    private companion object {
        private const val URL_V2 = "/api/v2/person"
        private const val URL_V4 = "/api/v4/person"
        private const val URL_V5 = "/api/v5/person"

        @Language("json")
        private const val RESPONSE_BODY_V2 = """{
    "navn": "Fornavn1",
    "foedselsdato": "1963-12-31",
    "sivilstand": "SKILT"
}"""

        @Language("json")
        private const val RESPONSE_BODY_V4 = """{
    "navn": "Fornavn1",
    "foedselsdato": "1963-12-31",
    "sivilstand": "SKILT",
    "pensjoneringAldre": {
        "normertPensjoneringsalder": {
            "aar": 67,
            "maaneder": 0
        },
        "nedreAldersgrense": {
            "aar": 62,
            "maaneder": 0
        }
    }
}"""

        @Language("json")
        private const val RESPONSE_BODY_V5 = """{
    "navn": "Fornavn1",
    "foedselsdato": "1963-12-31",
    "sivilstand": "SKILT",
    "pensjoneringAldre": {
        "normertPensjoneringsalder": {
            "aar": 67,
            "maaneder": 1
        },
        "nedreAldersgrense": {
            "aar": 62,
            "maaneder": 1
        },
        "oevreAldersgrense": {
            "aar": 75,
            "maaneder": 1
        }
    }
}"""
    }
}
