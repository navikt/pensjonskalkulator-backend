package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.ShadowTilgangComparator
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.TjenestepensjonSimuleringController
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

@WebMvcTest(TjenestepensjonSimuleringController::class)
@Import(MockSecurityConfiguration::class)
class TjenestepensjonSimuleringControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean(relaxed = true)
    private lateinit var service: TjenestepensjonSimuleringService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockkBean(relaxed = true)
    private lateinit var shadowTilgangComparator: ShadowTilgangComparator

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

        test("simuler offentlig tjenestepensjon V2") {
            every { service.hentTjenestepensjonSimulering(any()) } returns RESULTAT_OK_V2

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(REQUEST_BODY_V2)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_OK_V2))
        }

        test("simuler offentlig tjenestepensjon hvor det feiler hos tp-ordning") {
            every { service.hentTjenestepensjonSimulering(any()) } returns RESULTAT_TEKNISK_FEIL_V2

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(REQUEST_BODY_V2)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_TEKNISK_FEIL_V2))
        }

        test("simuler offentlig tjenestepensjon naar bruker ikke er medlem") {
            every { service.hentTjenestepensjonSimulering(any()) } returns RESULTAT_BRUKER_ER_IKKE_MEDLEM_V2

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(REQUEST_BODY_V2)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_BRUKER_ER_IKKE_MEDLEM_V2))
        }

        test("simuler offentlig tjenestepensjon naar bruker er medlem hos TP-ordning som ikke stoettes") {
            every { service.hentTjenestepensjonSimulering(any()) } returns RESULTAT_TP_ORDNING_STOETTES_IKKE_V2

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(REQUEST_BODY_V2)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_TP_ORDNING_STOETTES_IKKE_V2))
        }

        test("simuler offentlig tjenestepensjon naar TP-ordning returnerer tom respons") {
            every { service.hentTjenestepensjonSimulering(any()) } returns RESULTAT_TOM_RESPONS_FRA_TP_ORDNING_V2

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(REQUEST_BODY_V2)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(RESPONSE_BODY_TOM_RESPONS_FRA_TP_ORDNING_V2))
        }

        test("simuler offentlig tjenestepensjon feiler") {
            every { service.hentTjenestepensjonSimulering(any()) } throws EgressException("Pesys tok kvelden")

            mvc.perform(
                post(URL_V2)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(REQUEST_BODY_V2)
            )
                .andExpect(status().is5xxServerError())
        }
    }

    private companion object {
        private const val URL_V2 = "/api/v2/simuler-oftp"

        @Language("json")
        private const val RESPONSE_BODY_OK_V2 = """{
    "simuleringsresultatStatus": "OK",
    "muligeTpLeverandoerListe": [
        "Statens pensjonskasse"
    ],
    "simulertTjenestepensjon": {
        "tpLeverandoer": "Statens Pensjonskasse",
        "simuleringsresultat": {
            "utbetalingsperioder": [
                {
                    "startAlder": {
                        "aar": 63,
                        "maaneder": 0
                    },
                    "sluttAlder": {
                        "aar": 66,
                        "maaneder": 11
                    },
                    "aarligUtbetaling": 2640000
                },
                {
                    "startAlder": {
                        "aar": 67,
                        "maaneder": 0
                    },
                    "sluttAlder": {
                        "aar": 72,
                        "maaneder": 0
                    },
                    "aarligUtbetaling": 3000000
                },
                {
                    "startAlder": {
                        "aar": 72,
                        "maaneder": 1
                    },
                    "aarligUtbetaling": 1200000
                }
            ],
            "betingetTjenestepensjonErInkludert": true
        }
    }
}"""

        @Language("json")
        private const val REQUEST_BODY_V2 = """{
    "foedselsdato": "1964-01-02",
    "aarligInntektFoerUttakBeloep": 900000,
    "gradertUttak": {
        "uttaksalder": { "aar": 63, "maaneder": 0 },
        "aarligInntektVsaPensjonBeloep": 75000
    },
    "heltUttak": {
        "uttaksalder": { "aar": 67, "maaneder": 1 },
        "aarligInntektVsaPensjon": {
           "beloep": 50000,
           "sluttAlder": { "aar": 75, "maaneder": 0 } 
        }
    },
    "utenlandsperiodeListe": [
        {
            "fom": "2020-01-01",
            "tom": "2021-01-01"
        }
    ],
    "epsHarPensjon": false,
    "epsHarInntektOver2G": false,
    "brukerBaOmAfp": false
}"""

        private val RESULTAT_OK_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.OK, feilmelding = null),
            simuleringsResultat = SimuleringsResultat(
                tpOrdning = "Statens Pensjonskasse",
                tpNummer = "111111",
                perioder = listOf(
                    Utbetaling(
                        startAlder = Alder(63, 0),
                        sluttAlder = Alder(66, 11),
                        maanedligBeloep = 220000
                    ),
                    Utbetaling(
                        startAlder = Alder(67, 0),
                        sluttAlder = Alder(72, 0),
                        maanedligBeloep = 250000
                    ),
                    Utbetaling(
                        startAlder = Alder(72, 1),
                        sluttAlder = null,
                        maanedligBeloep = 100000
                    ),
                ),
                betingetTjenestepensjonInkludert = true
            ),
            tpOrdninger = listOf("Statens pensjonskasse")
        )

        @Language("json")
        private const val RESPONSE_BODY_TEKNISK_FEIL_V2 = """{
        "simuleringsresultatStatus": "TEKNISK_FEIL",
        "muligeTpLeverandoerListe": [
            "Bodø kommunale pensjonskasse",
            "Statens pensjonskasse"
        ]
    }"""

        private val RESULTAT_TEKNISK_FEIL_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(
                resultatType = ResultatType.TEKNISK_FEIL,
                feilmelding = "Noe gikk galt"
            ),
            tpOrdninger = listOf("Bodø kommunale pensjonskasse", "Statens pensjonskasse")
        )

        @Language("json")
        private const val RESPONSE_BODY_BRUKER_ER_IKKE_MEDLEM_V2 = """{
        "simuleringsresultatStatus": "BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING",
        "muligeTpLeverandoerListe": []
    }"""

        private val RESULTAT_BRUKER_ER_IKKE_MEDLEM_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(
                resultatType = ResultatType.IKKE_MEDLEM,
                feilmelding = "Ikke medlem"
            )
        )

        @Language("json")
        private const val RESPONSE_BODY_TP_ORDNING_STOETTES_IKKE_V2 = """{
        "simuleringsresultatStatus": "TP_ORDNING_STOETTES_IKKE",
            "muligeTpLeverandoerListe": ["Pensjonstrygden uten navn"]
    }"""

        private val RESULTAT_TP_ORDNING_STOETTES_IKKE_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(
                resultatType = ResultatType.TP_ORDNING_STOETTES_IKKE,
                feilmelding = "TP-ordning støttes ikke"
            ),
            tpOrdninger = listOf("Pensjonstrygden uten navn")
        )

        @Language("json")
        private const val RESPONSE_BODY_TOM_RESPONS_FRA_TP_ORDNING_V2 = """{
        "simuleringsresultatStatus": "TOM_SIMULERING_FRA_TP_ORDNING",
            "muligeTpLeverandoerListe": ["Pensjonstrygden med navn"]
    }"""

        private val RESULTAT_TOM_RESPONS_FRA_TP_ORDNING_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(
                resultatType = ResultatType.TOM_RESPONS,
                feilmelding = "Ingen utbetalingsperioder fra TP-ordning"
            ),
            tpOrdninger = listOf("Pensjonstrygden med navn")
        )
    }
}
