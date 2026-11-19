package no.nav.pensjon.kalkulator.tjenestepensjon.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.MaanedligBeloep
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.json.JsonCompareMode.STRICT
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(TjenestepensjonController::class)
@Import(MockSecurityConfiguration::class)
class TjenestepensjonControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var tjenestepensjonService: TjenestepensjonService

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

        test("har offentlig tjenestepensjonsforhold") {
            every { tjenestepensjonService.harTjenestepensjonsforhold() } returns true

            mvc.perform(
                get(MEDLEMSKAP_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(MEDLEMSKAP_RESPONSE_BODY))
        }

        test("hent medlemskap i offentlige tjenestepensjonsordninger") {
            every { tjenestepensjonService.hentMedlemskapITjenestepensjonsordninger() } returns
                    listOf(
                        "Maritim pensjonskasse",
                        "Statens pensjonskasse",
                        "Kommunal Landspensjonskasse"
                    )

            mvc.perform(
                get(MEDLEMSKAP_URL_V1)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(MEDLEMSKAP_RESPONSE_BODY_V1))
        }

        test("hent løpende livsvarig offentlig AFP, versjon 2") {
            val fom = LocalDate.of(2025, 1, 1)
            every { tjenestepensjonService.hentAfpOffentligLivsvarigDetaljer() } returns
                    AfpOffentligLivsvarigResult(
                        afpInnvilget = true,
                        virkningFom = fom,
                        maanedligBeloepListe = listOf(MaanedligBeloep(fom, 15000)),
                        sistBenyttetGrunnbeloep = 123000,
                        tpNummer = "3010",
                        tpOrdningNavn = "Statens pensjonskasse"
                    )

            mvc.perform(
                get(LIVSVARIG_OFFENTLIG_AFP_URL_V2)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(LIVSVARIG_OFFENTLIG_AFP_RESPONSE_BODY_V2))
                .andExpect(jsonPath("$.tpNummer").doesNotExist())
                .andExpect(jsonPath("$.tpOrdningNavn").doesNotExist())
        }

        test("hent løpende livsvarig offentlig AFP, versjon 3") {
            val fom = LocalDate.of(2025, 10, 1)
            every { tjenestepensjonService.hentAfpOffentligLivsvarigDetaljer() } returns
                    AfpOffentligLivsvarigResult(
                        afpInnvilget = true,
                        virkningFom = fom,
                        maanedligBeloepListe = listOf(
                            MaanedligBeloep(fom, beloep = 15000),
                            MaanedligBeloep(fom = LocalDate.of(2026, 1, 1), beloep = 16000),
                        ),
                        sistBenyttetGrunnbeloep = 123000,
                        tpNummer = "3010",
                        tpOrdningNavn = "Statens pensjonskasse"
                    )

            mvc.perform(
                get(LIVSVARIG_OFFENTLIG_AFP_URL_V3)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(LIVSVARIG_OFFENTLIG_AFP_RESPONSE_BODY_V3))
                .andExpect(jsonPath("$.tpNummer").doesNotExist())
                .andExpect(jsonPath("$.tpOrdningNavn").doesNotExist())
        }

        test("hent livsvarig offentlig AFP med TP-ordning, versjon 4") {
            val fom = LocalDate.of(2025, 10, 1)
            every { tjenestepensjonService.hentAfpOffentligLivsvarigDetaljerMedTpOrdning() } returns
                    AfpOffentligLivsvarigResult(
                        afpInnvilget = true,
                        virkningFom = fom,
                        maanedligBeloepListe = listOf(
                            MaanedligBeloep(fom, 15000),
                            MaanedligBeloep(LocalDate.of(2026, 1, 1), 16000)
                        ),
                        sistBenyttetGrunnbeloep = 123000,
                        tpNummer = "3010",
                        tpOrdningNavn = "Statens pensjonskasse"
                    )

            mvc.perform(get(LIVSVARIG_OFFENTLIG_AFP_URL_V4).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(LIVSVARIG_OFFENTLIG_AFP_RESPONSE_BODY_V4, STRICT))
        }

        test("hent livsvarig offentlig AFP uten ordning, versjon 4") {
            every { tjenestepensjonService.hentAfpOffentligLivsvarigDetaljerMedTpOrdning() } returns
                    AfpOffentligLivsvarigResult(null, null, emptyList(), null)

            mvc.perform(get(LIVSVARIG_OFFENTLIG_AFP_URL_V4).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""{
                    "afpInnvilget": null,
                    "maanedligBeloepListe": [],
                    "virkningFom": null,
                    "sistBenyttetGrunnbeloep": null,
                    "tpNummer": null,
                    "tpOrdningNavn": null
                }""", STRICT))
        }

        test("hent livsvarig offentlig AFP som ikke er innvilget, versjon 4") {
            every { tjenestepensjonService.hentAfpOffentligLivsvarigDetaljerMedTpOrdning() } returns
                    AfpOffentligLivsvarigResult(
                        afpInnvilget = false,
                        virkningFom = null,
                        maanedligBeloepListe = emptyList(),
                        sistBenyttetGrunnbeloep = null,
                        tpNummer = "3020",
                        tpOrdningNavn = "Kommunal Landspensjonskasse"
                    )

            mvc.perform(get(LIVSVARIG_OFFENTLIG_AFP_URL_V4).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("""{
                    "afpInnvilget": false,
                    "maanedligBeloepListe": [],
                    "virkningFom": null,
                    "sistBenyttetGrunnbeloep": null,
                    "tpNummer": "3020",
                    "tpOrdningNavn": "Kommunal Landspensjonskasse"
                }""", STRICT))
        }

        test("returner 503 ved feil i ekstern tjeneste, versjon 4") {
            every { tjenestepensjonService.hentAfpOffentligLivsvarigDetaljerMedTpOrdning() } throws
                    EgressException("TP-registeret er utilgjengelig", statusCode = HttpStatus.SERVICE_UNAVAILABLE)

            mvc.perform(get(LIVSVARIG_OFFENTLIG_AFP_URL_V4).with(csrf()))
                .andExpect(status().isServiceUnavailable())
        }

        test("returner 500 ved klientfeil mot ekstern tjeneste, versjon 4") {
            every { tjenestepensjonService.hentAfpOffentligLivsvarigDetaljerMedTpOrdning() } throws
                    EgressException("TP-ordning ikke funnet", statusCode = HttpStatus.NOT_FOUND)

            mvc.perform(get(LIVSVARIG_OFFENTLIG_AFP_URL_V4).with(csrf()))
                .andExpect(status().isInternalServerError())
        }
    }

    private companion object {
        private const val MEDLEMSKAP_URL = "/api/tpo-medlemskap"
        private const val MEDLEMSKAP_URL_V1 = "/api/v1/tpo-medlemskap"
        private const val LIVSVARIG_OFFENTLIG_AFP_URL_V2 = "/api/v2/tpo-livsvarig-offentlig-afp"
        private const val LIVSVARIG_OFFENTLIG_AFP_URL_V3 = "/api/v3/tpo-livsvarig-offentlig-afp"
        private const val LIVSVARIG_OFFENTLIG_AFP_URL_V4 = "/api/v4/tpo-livsvarig-offentlig-afp"

        @Language("json")
        private const val MEDLEMSKAP_RESPONSE_BODY = """{
	"harTjenestepensjonsforhold": true
}"""

        @Language("json")
        private const val MEDLEMSKAP_RESPONSE_BODY_V1 = """{
        "tpLeverandoerListe": ["Maritim pensjonskasse", "Statens pensjonskasse", "Kommunal Landspensjonskasse"]
    }"""

        @Language("json")
        private const val LIVSVARIG_OFFENTLIG_AFP_RESPONSE_BODY_V2 = """{
        "afpStatus": true,
        "maanedligBeloep": 15000
    }"""

        @Language("json")
        private const val LIVSVARIG_OFFENTLIG_AFP_RESPONSE_BODY_V3 = """{
        "afpInnvilget": true,
        "maanedligBeloepListe": [
            {
            "virkningFom":"2025-10-01",
            "beloep":15000
            },
            {
            "virkningFom":"2026-01-01",
            "beloep":16000
            }
        ],
        "virkningFom":"2025-10-01",
        "sistBenyttetGrunnbeloep":123000
    }"""

        @Language("json")
        private const val LIVSVARIG_OFFENTLIG_AFP_RESPONSE_BODY_V4 = """{
        "afpInnvilget": true,
        "maanedligBeloepListe": [
            {"virkningFom": "2025-10-01", "beloep": 15000},
            {"virkningFom": "2026-01-01", "beloep": 16000}
        ],
        "virkningFom": "2025-10-01",
        "sistBenyttetGrunnbeloep": 123000,
        "tpNummer": "3010",
        "tpOrdningNavn": "Statens pensjonskasse"
    }"""
    }
}
