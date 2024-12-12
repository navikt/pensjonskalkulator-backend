package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.anyNonNull
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.TjenestepensjonSimuleringController
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TjenestepensjonSimuleringController::class)
@Import(MockSecurityConfiguration::class)
class TjenestepensjonSimuleringControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var service: TjenestepensjonSimuleringService

    @MockitoBean
    private lateinit var traceAid: TraceAid

    @MockitoBean
    private lateinit var pidExtractor: PidExtractor

    @MockitoBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockitoBean
    private lateinit var auditor: Auditor

    @Test
    fun `simuler offentlig tjenestepensjon V1`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_OK_V2)

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V1)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_OK_V1))
    }

    @Test
    fun `simuler offentlig tjenestepensjon V2`() {
        `when`(service.hentTjenestepensjonSimuleringV2(anyNonNull())).thenReturn(RESULTAT_OK_V2)

        mvc.perform(
            post(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V2)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_OK_V1))
    }

    @Test
    fun `simuler offentlig tjenestepensjon hvor det feiler hos tp-ordning`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_TEKNISK_FEIL_V2)

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V1)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_TEKNISK_FEIL_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon naar bruker ikke er medlem`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_BRUKER_ER_IKKE_MEDLEM_V2)

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V1)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_BRUKER_ER_IKKE_MEDLEM_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon naar bruker er medlem hos TP-ordning som ikke stoettes`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_TP_ORDNING_STOETTES_IKKE_V2)

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V1)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_TP_ORDNING_STOETTES_IKKE_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon naar TP-ordning returnerer tom respons`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_TOM_RESPONS_FRA_TP_ORDNING_V2)

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V1)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_TOM_RESPONS_FRA_TP_ORDNING_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon feiler`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenThrow(EgressException("Pesys tok kvelden"))

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V1)
        )
            .andExpect(status().is5xxServerError())
    }

    private companion object {
        private const val URL_V1 = "/api/v1/simuler-oftp"
        private const val URL_V2 = "/api/v2/simuler-oftp"
        @Language("json")
        private const val RESPONSE_BODY_OK_V1 = """{
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
        private const val REQUEST_BODY_V1 = """{
    "foedselsdato": "1964-01-02",
    "uttaksalder": {
    "aar": 63,
    "maaneder": 0
    },
    "aarligInntektFoerUttakBeloep": 900000,
    "antallAarIUtlandetEtter16": 0,
    "epsHarPensjon": false,
    "epsHarInntektOver2G": false,
    "brukerBaOmAfpOffentlig": false
}"""

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
                perioder = listOf(
                    Utbetaling(startAlder = Alder(63, 0), sluttAlder = Alder(66, 11),
                        maanedligBeloep = 220000),
                    Utbetaling(startAlder = Alder(67, 0), sluttAlder = Alder(72, 0),
                        maanedligBeloep = 250000),
                    Utbetaling(startAlder = Alder(72, 1), sluttAlder = null,
                        maanedligBeloep = 100000),
                ),
                betingetTjenestepensjonInkludert = true),
            tpOrdninger = listOf("Statens pensjonskasse")
        )

        private const val RESPONSE_BODY_TEKNISK_FEIL_V2 = """{
        "simuleringsresultatStatus": "TEKNISK_FEIL",
        "muligeTpLeverandoerListe": [
            "Bodø kommunale pensjonskasse",
            "Statens pensjonskasse"
        ]
    }"""
        private val RESULTAT_TEKNISK_FEIL_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.TEKNISK_FEIL, feilmelding = "Noe gikk galt"),
            tpOrdninger = listOf("Bodø kommunale pensjonskasse","Statens pensjonskasse")
        )

        private const val RESPONSE_BODY_BRUKER_ER_IKKE_MEDLEM_V2 = """{
        "simuleringsresultatStatus": "BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING",
        "muligeTpLeverandoerListe": []
    }"""
        private val RESULTAT_BRUKER_ER_IKKE_MEDLEM_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.IKKE_MEDLEM, feilmelding = "Ikke medlem")
        )

        private const val RESPONSE_BODY_TP_ORDNING_STOETTES_IKKE_V2 = """{
        "simuleringsresultatStatus": "TP_ORDNING_STOETTES_IKKE",
            "muligeTpLeverandoerListe": ["Pensjonstrygden uten navn"]
    }"""
        private val RESULTAT_TP_ORDNING_STOETTES_IKKE_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.TP_ORDNING_STOETTES_IKKE, feilmelding = "TP-ordning støttes ikke"),
            tpOrdninger = listOf("Pensjonstrygden uten navn")
        )

        private const val RESPONSE_BODY_TOM_RESPONS_FRA_TP_ORDNING_V2 = """{
        "simuleringsresultatStatus": "TOM_SIMULERING_FRA_TP_ORDNING",
            "muligeTpLeverandoerListe": ["Pensjonstrygden med navn"]
    }"""
        private val RESULTAT_TOM_RESPONS_FRA_TP_ORDNING_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.TOM_RESPONS, feilmelding = "Ingen utbetalingsperioder fra TP-ordning"),
            tpOrdninger = listOf("Pensjonstrygden med navn")
        )

    }
}