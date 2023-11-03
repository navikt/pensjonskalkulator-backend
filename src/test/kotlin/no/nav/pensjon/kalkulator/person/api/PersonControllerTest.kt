package no.nav.pensjon.kalkulator.person.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.skiltPerson
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.SkjermingService
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PersonController::class)
@Import(MockSecurityConfiguration::class)
class PersonControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: PersonService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var skjermingService: SkjermingService

    @Test
    fun person() {
        `when`(service.getPerson()).thenReturn(skiltPerson())

        mvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {
        private const val URL = "/api/person"

        private const val RESPONSE_BODY = """{
        "fornavn": "Fornavn1",
        "sivilstand": "SKILT"
    }"""
    }
}
