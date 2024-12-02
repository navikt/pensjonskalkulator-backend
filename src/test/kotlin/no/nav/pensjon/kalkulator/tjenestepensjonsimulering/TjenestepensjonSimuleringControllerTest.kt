package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

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
    fun `simuler offentlig tjenestepensjon`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_OK_V2)

        mvc.perform(
            post(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V2)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_OK_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon hvor det feiler hos tp-ordning`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_TEKNISK_FEIL_V2)

        mvc.perform(
            post(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V2)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_TEKNISK_FEIL_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon naar bruker ikke er medlem`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_BRUKER_ER_IKKE_MEDLEM_V2)

        mvc.perform(
            post(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V2)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_BRUKER_ER_IKKE_MEDLEM_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon naar bruker er medlem hos TP-ordning som ikke stoettes`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_TP_ORDNING_STOETTES_IKKE_V2)

        mvc.perform(
            post(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V2)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_TP_ORDNING_STOETTES_IKKE_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon naar TP-ordning returnerer tom respons`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenReturn(RESULTAT_TOM_RESPONS_FRA_TP_ORDNING_V2)

        mvc.perform(
            post(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V2)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(RESPONSE_BODY_TOM_RESPONS_FRA_TP_ORDNING_V2))
    }

    @Test
    fun `simuler offentlig tjenestepensjon feiler`() {
        `when`(service.hentTjenestepensjonSimulering(anyNonNull())).thenThrow(EgressException("Pesys tok kvelden"))

        mvc.perform(
            post(URL_V2)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_V2)
        )
            .andExpect(status().is5xxServerError())
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
                    "aar": 63,
                    "beloep": 227844
                },
                {
                    "aar": 64,
                    "beloep": 227844
                },
                {
                    "aar": 65,
                    "beloep": 227844
                },
                {
                    "aar": 66,
                    "beloep": 227844
                },
                {
                    "aar": 67,
                    "beloep": 208560
                },
                {
                    "aar": 68,
                    "beloep": 208560
                },
                {
                    "aar": 69,
                    "beloep": 208560
                },
                {
                    "aar": 70,
                    "beloep": 208560
                },
                {
                    "aar": 71,
                    "beloep": 208560
                },
                {
                    "aar": 72,
                    "beloep": 208560
                },
                {
                    "aar": 73,
                    "beloep": 208560
                },
                {
                    "aar": 74,
                    "beloep": 208560
                },
                {
                    "aar": 75,
                    "beloep": 208560
                },
                {
                    "aar": 76,
                    "beloep": 208560
                },
                {
                    "aar": 77,
                    "beloep": 208560
                },
                {
                    "aar": 78,
                    "beloep": 208560
                },
                {
                    "aar": 79,
                    "beloep": 208560
                },
                {
                    "aar": 80,
                    "beloep": 208560
                },
                {
                    "aar": 81,
                    "beloep": 208560
                },
                {
                    "aar": 82,
                    "beloep": 208560
                },
                {
                    "aar": 83,
                    "beloep": 208560
                },
                {
                    "aar": 84,
                    "beloep": 208560
                },
                {
                    "aar": 85,
                    "beloep": 208560
                }
            ],
            "betingetTjenestepensjonErInkludert": true
        }
    }
}"""
        private const val REQUEST_BODY_V2 = """{
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

        private val RESULTAT_OK_V2 = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.OK, feilmelding = null),
            simuleringsResultat = SimuleringsResultat(
                tpOrdning = "Statens Pensjonskasse",
                perioder = listOf(
                    Utbetaling(aar = 63, beloep = 227844),
                    Utbetaling(aar = 64, beloep = 227844),
                    Utbetaling(aar = 65, beloep = 227844),
                    Utbetaling(aar = 66, beloep = 227844),
                    Utbetaling(aar = 67, beloep = 208560),
                    Utbetaling(aar = 68, beloep = 208560),
                    Utbetaling(aar = 69, beloep = 208560),
                    Utbetaling(aar = 70, beloep = 208560),
                    Utbetaling(aar = 71, beloep = 208560),
                    Utbetaling(aar = 72, beloep = 208560),
                    Utbetaling(aar = 73, beloep = 208560),
                    Utbetaling(aar = 74, beloep = 208560),
                    Utbetaling(aar = 75, beloep = 208560),
                    Utbetaling(aar = 76, beloep = 208560),
                    Utbetaling(aar = 77, beloep = 208560),
                    Utbetaling(aar = 78, beloep = 208560),
                    Utbetaling(aar = 79, beloep = 208560),
                    Utbetaling(aar = 80, beloep = 208560),
                    Utbetaling(aar = 81, beloep = 208560),
                    Utbetaling(aar = 82, beloep = 208560),
                    Utbetaling(aar = 83, beloep = 208560),
                    Utbetaling(aar = 84, beloep = 208560),
                    Utbetaling(aar = 85, beloep = 208560)),
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