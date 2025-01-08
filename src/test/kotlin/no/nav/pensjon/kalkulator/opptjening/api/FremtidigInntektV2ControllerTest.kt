package no.nav.pensjon.kalkulator.opptjening.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
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
import java.math.BigDecimal

@WebMvcTest(InntektController::class)
@Import(MockSecurityConfiguration::class)
class FremtidigInntektV2ControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var service: InntektService

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
    fun fetchInntektsforhold() {
        val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2021, BigDecimal("123000"))
        `when`(service.sistePensjonsgivendeInntekt()).thenReturn(inntekt)

        mvc.perform(
            get(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/inntekt"

        @Language("json")
        private const val RESPONSE_BODY = """{
	"beloep": 123000,
	"aar": 2021
}"""
    }
}
