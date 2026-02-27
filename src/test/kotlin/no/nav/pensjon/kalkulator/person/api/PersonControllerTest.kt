package no.nav.pensjon.kalkulator.person.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.personWithPensjoneringAldre
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.PersonService
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
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
            every { pidExtractor.pid() } returns pid
            every { adresseService.adressebeskyttelseGradering(any()) } returns AdressebeskyttelseGradering.UGRADERT
            every { auditor.audit(any(), any()) } returns Unit
        }

        test("'person' endpoint version 6") {
            every { personService.getPerson() } returns personWithPensjoneringAldre()

            mvc.perform(
                get(URL_V6)
                    .with(csrf())
                    .content("")
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_V6))
        }
    }

    private companion object {
        private const val URL_V6 = "/api/v6/person"

        @Language("json")
        private const val RESPONSE_BODY_V6 = """{
    "navn": "Fornavn1 Etternavn1",
    "fornavn": "Fornavn1",
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
