package no.nav.pensjon.kalkulator.normalder.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.normalder.AldersgrenseSpec
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
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

@WebMvcTest(NormertPensjonsalderController::class)
@Import(MockSecurityConfiguration::class)
class NormertPensjonsalderControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var service: NormertPensjonsalderService

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

        test("'aldersgrense' endpoint version 1 with birth year 1963") {
            val spec = AldersgrenseSpec(aarskull = 1963)
            val aldersgrenser = Aldersgrenser(
                aarskull = 1963,
                normalder = Alder(aar = 67, maaneder = 0),
                nedreAlder = Alder(aar = 62, maaneder = 0),
                oevreAlder = Alder(aar = 75, maaneder = 0),
                verdiStatus = VerdiStatus.FAST
            )

            every { service.aldersgrenser(spec) } returns aldersgrenser

            mvc.perform(
                post(URL_V1)
                    .with(csrf())
                    .content(REQUEST_BODY_1963)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_1963))
        }

        test("'aldersgrense' endpoint version 1 with birth year 1970") {
            val spec = AldersgrenseSpec(aarskull = 1970)
            val aldersgrenser = Aldersgrenser(
                aarskull = 1970,
                normalder = Alder(aar = 67, maaneder = 0),
                nedreAlder = Alder(aar = 62, maaneder = 0),
                oevreAlder = Alder(aar = 75, maaneder = 0),
                verdiStatus = VerdiStatus.FAST
            )

            every { service.aldersgrenser(spec) } returns aldersgrenser

            mvc.perform(
                post(URL_V1)
                    .with(csrf())
                    .content(REQUEST_BODY_1970)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_1970))
        }

        test("'aldersgrense' endpoint V2 with birth year 1963") {
            val spec = AldersgrenseSpec(aarskull = 1963)
            val aldersgrenser = Aldersgrenser(
                aarskull = 1963,
                normalder = Alder(aar = 67, maaneder = 0),
                nedreAlder = Alder(aar = 62, maaneder = 0),
                oevreAlder = Alder(aar = 95, maaneder = 11),
                verdiStatus = VerdiStatus.FAST
            )

            every { service.aldersgrenser(spec) } returns aldersgrenser

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .content(REQUEST_BODY_1963)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_1963_V2))
        }

        test("'aldersgrense' endpoint V2 with birth year 1970") {
            val spec = AldersgrenseSpec(aarskull = 1970)
            val aldersgrenser = Aldersgrenser(
                aarskull = 1970,
                normalder = Alder(aar = 67, maaneder = 0),
                nedreAlder = Alder(aar = 62, maaneder = 0),
                oevreAlder = Alder(aar = 75, maaneder = 0),
                verdiStatus = VerdiStatus.FAST
            )

            every { service.aldersgrenser(spec) } returns aldersgrenser

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .content(REQUEST_BODY_1970)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_1970_V2))
        }
    }

    private companion object {
        private const val URL_V1 = "/api/v1/aldersgrense"
        private const val URL_V2 = "/api/v2/aldersgrense"

        @Language("json")
        private const val REQUEST_BODY_1963 = """{
            "foedselsdato": 1963
        }"""

        @Language("json")
        private const val REQUEST_BODY_1970 = """{
            "foedselsdato": 1970
        }"""

        @Language("json")
        private const val RESPONSE_BODY_1963 = """{
            "normertPensjoneringsalder": {
                "aar": 67,
                "maaneder": 0
            },
            "nedreAldersgrense": {
                "aar": 62,
                "maaneder": 0
            }
        }"""

        @Language("json")
        private const val RESPONSE_BODY_1970 = """{
            "normertPensjoneringsalder": {
                "aar": 67,
                "maaneder": 0
            },
            "nedreAldersgrense": {
                "aar": 62,
                "maaneder": 0
            }
        }"""

        @Language("json")
        private const val RESPONSE_BODY_1963_V2 = """{
            "normertPensjoneringsalder": {
                "aar": 67,
                "maaneder": 0
            },
            "nedreAldersgrense": {
                "aar": 62,
                "maaneder": 0
            },
            "oevreAldersgrense": {
                "aar": 95,
                "maaneder": 11
            }
        }"""

        @Language("json")
        private const val RESPONSE_BODY_1970_V2 = """{
            "normertPensjoneringsalder": {
                "aar": 67,
                "maaneder": 0
            },
            "nedreAldersgrense": {
                "aar": 62,
                "maaneder": 0
            },
            "oevreAldersgrense": {
                "aar": 75,
                "maaneder": 0
            }
        }"""
    }
}
