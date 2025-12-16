package no.nav.pensjon.kalkulator.avtale.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.avtale.InntektSpec
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtaler
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PensjonsavtaleController::class)
@Import(MockSecurityConfiguration::class)
class PensjonsavtaleControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var avtaleService: PensjonsavtaleService

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

        should("hente avtaler V3") {
            every { avtaleService.fetchAvtaler(avtaleSpecMedTidsbegrensetInntekt()) } returns pensjonsavtaler()

            mvc.perform(
                post(URL_V3)
                    .with(csrf())
                    .content(REQUEST_BODY_V3)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY))
        }
    }

    private companion object {
        private const val URL_V3 = "/api/v3/pensjonsavtaler"

        @Language("json")
        private val REQUEST_BODY_V2 = """{
	"aarligInntektFoerUttakBeloep": 456000,
	"uttaksperioder": [{
        "startAlder": {
	  	    "aar": 67,
		    "maaneder": 1
	    },
		"grad": 80,
		"aarligInntektVsaPensjon": {
	  	    "beloep": 123000,
		    "sluttAlder": {
	  	       "aar": 67,
		       "maaneder": 1
	        }
	    }
	}, {
       "startAlder": {
	  	    "aar": 70,
		    "maaneder": 1
	    },
		"grad": 100,
		"aarligInntektVsaPensjon": {
	  	    "beloep": 45000,
		    "sluttAlder": {
	  	       "aar": 69,
		       "maaneder": 1
	        }
	    }
	}],
    "harAfp": false,
    "harEpsPensjon": true,
    "harEpsPensjonsgivendeInntektOver2G": true,
    "antallAarIUtlandetEtter16": 0,
    "sivilstand": "UGIFT"
}"""

        @Language("json")
        private val REQUEST_BODY_V3 = """{
	"aarligInntektFoerUttakBeloep": 456000,
	"uttaksperioder": [{
        "startAlder": {
	  	    "aar": 67,
		    "maaneder": 1
	    },
		"grad": 80,
		"aarligInntektVsaPensjon": {
	  	    "beloep": 123000,
		    "sluttAlder": {
	  	       "aar": 67,
		       "maaneder": 1
	        }
	    }
	}, {
       "startAlder": {
	  	    "aar": 70,
		    "maaneder": 1
	    },
		"grad": 100,
		"aarligInntektVsaPensjon": {
	  	    "beloep": 45000,
		    "sluttAlder": {
	  	       "aar": 69,
		       "maaneder": 1
	        }
	    }
	}],
    "harAfp": false,
    "epsHarPensjon": true,
    "epsHarInntektOver2G": true,
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

        private fun avtaleSpecMedTidsbegrensetInntekt() =
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = 456000,
                uttaksperioder = listOf(gradertUttak(), heltUttak()),
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                sivilstand = Sivilstand.UGIFT
            )

        private fun gradertUttak() =
            UttaksperiodeSpec(
                startAlder = Alder(aar = 67, maaneder = 1),
                grad = Uttaksgrad.AATTI_PROSENT,
                aarligInntekt = InntektSpec(
                    aarligBeloep = 123000,
                    tomAlder = Alder(aar = 67, maaneder = 1)
                )
            )

        private fun heltUttak() =
            UttaksperiodeSpec(
                startAlder = Alder(aar = 70, maaneder = 1),
                grad = Uttaksgrad.HUNDRE_PROSENT,
                aarligInntekt = InntektSpec(
                    aarligBeloep = 45000,
                    tomAlder = Alder(aar = 69, maaneder = 1)
                )
            )
    }
}
