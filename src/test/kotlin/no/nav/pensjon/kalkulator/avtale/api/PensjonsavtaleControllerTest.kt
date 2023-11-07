package no.nav.pensjon.kalkulator.avtale.api

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleServiceTest.Companion.pensjonsavtaleSpec
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtaler
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

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun fetchAvtaler() {
        `when`(avtaleService.fetchAvtaler(pensjonsavtaleSpec())).thenReturn(pensjonsavtaler())

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

        private const val URL = "/api/v1/pensjonsavtaler"

        @Language("json")
        private val REQUEST_BODY = """{
	"aarligInntektFoerUttak": 456000,
	"uttaksperioder": [{
        "startAlder": {
	  	    "aar": 67,
		    "maaneder": 1
	    },
		"grad": 80,
		"aarligInntekt": 123000
	}, {
       "startAlder": {
	  	    "aar": 70,
		    "maaneder": 1
	    },
		"grad": 100,
		"aarligInntekt": 45000
	}],
	"antallInntektsaarEtterUttak": 2,
    "harAfp": false,
    "harEpsPensjon": true,
    "harEpsPensjonsgivendeInntektOver2G": true,
    "antallAarIUtlandetEtter16": 0,
    "sivilstand": "UGIFT"
}"""

        @Language("json")
        private const val RESPONSE_BODY = """{
	"avtaler": [{
		"produktbetegnelse": "produkt1",
		"kategori": "INDIVIDUELL_ORDNING",
		"startAar": 67,
		"sluttAar": 77,
		"utbetalingsperioder": [{
            "startAlder": {
	   	        "aar": 68,
	 	        "maaneder": 1
	        },
            "sluttAlder": {
	   	        "aar": 78,
	 	        "maaneder": 11
	        },
			"aarligUtbetaling": 123000,
			"grad": 100
		}]
	}],
	"utilgjengeligeSelskap": [{
		"navn": "selskap1",
		"heltUtilgjengelig": true
	}]
}"""
    }
}
