package no.nav.pensjon.kalkulator.person.api.v7

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.personWithPensjoneringAldre
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.PersonFacade
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PersonV7Controller::class)
@Import(MockSecurityConfiguration::class)
class PersonV7ControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var personService: PersonFacade

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
            every { pidExtractor.pid() } returns pid
            every { adresseService.adressebeskyttelseGradering(any()) } returns AdressebeskyttelseGradering.UGRADERT
            every { auditor.audit(any(), any()) } returns Unit
        }

        should("gi persondata i JSON-format") {
            every { personService.getPerson() } returns personWithPensjoneringAldre()

            mvc.perform(
                get(URL)
                    .with(csrf())
                    .content("")
            )
                .andExpect(status().isOk())
                .andExpect(content().json(PERSON_JSON))
        }
    }

    private companion object {
        private const val URL = "/api/v7/person"

        @Language("json")
        private const val PERSON_JSON = """{
    "navn": "Fornavn1 Etternavn1",
    "fornavn": "Fornavn1",
    "foedselsdato": "1963-12-31",
    "sivilstatus": "SAMBOER",
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
