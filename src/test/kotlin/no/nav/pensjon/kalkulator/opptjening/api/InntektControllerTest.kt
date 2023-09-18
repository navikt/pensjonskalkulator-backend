package no.nav.pensjon.kalkulator.opptjening.api

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
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
import java.math.BigDecimal

@WebMvcTest(InntektController::class)
@Import(MockSecurityConfiguration::class)
class InntektControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: InntektService

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
