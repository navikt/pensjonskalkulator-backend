package no.nav.pensjon.kalkulator.avtale.api

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtaler
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PensjonsavtaleController::class)
@Import(MockSecurityConfiguration::class)
class PensjonsavtaleControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var avtaleService: PensjonsavtaleService

    @Test
    fun fetchAvtaler() {
        `when`(avtaleService.fetchAvtaler(pensjonsavtaleSpecDto())).thenReturn(pensjonsavtaler())

        mvc.perform(
            post(URL)
                .with(csrf())
                .content(REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY))
    }

    private companion object {

        private const val URL = "/api/pensjonsavtaler"

        // Corresponds with REQUEST_BODY
        private fun pensjonsavtaleSpecDto() = PensjonsavtaleSpecDto(100000, uttaksperiodeSpec(), 1)

        private fun uttaksperiodeSpec() = UttaksperiodeSpec(67, 1, 80, 123000)

        @Language("json")
        private val REQUEST_BODY = """{
	"aarligInntektFoerUttak": 100000,
	"uttaksperiode": {
		"startAlder": 67,
		"startMaaned": 1,
		"grad": 80,
		"aarligInntekt": 123000
	},
	"antallInntektsaarEtterUttak": 1
}"""

        @Language("json")
        private const val RESPONSE_BODY = """{
	"avtaler": [{
		"produktbetegnelse": "produkt1",
		"kategori": "kategori1",
		"startAlder": 67,
		"sluttAlder": 77,
		"utbetalingsperiode": {
			"startAlder": 68,
			"startMaaned": 1,
			"sluttAlder": 78,
			"sluttMaaned": 12,
			"aarligUtbetaling": 123000,
			"grad": 100
		}
	}],
	"utilgjengeligeSelskap": [{
		"navn": "selskap1",
		"heltUtilgjengelig": true
	}]
}"""
    }
}
