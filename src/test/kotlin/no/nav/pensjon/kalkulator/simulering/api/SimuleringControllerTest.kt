package no.nav.pensjon.kalkulator.simulering.api

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(SimuleringController::class)
@Import(MockSecurityConfiguration::class)
class SimuleringControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var simuleringService: SimuleringService

    @MockitoBean
    private lateinit var anonymSimuleringService: AnonymSimuleringService

    @MockitoBean
    private lateinit var feature: FeatureToggleService

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
    fun `simulerer hel alderspensjon V8`() {
        val spec = impersonalHeltUttakSpec(SimuleringType.ALDERSPENSJON, epsHarInntektOver2G = true)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(heltUttakRequestBodyV8(SimuleringType.ALDERSPENSJON))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyForHeltUttakV8()))
    }

    @Test
    fun `simulerer alderspensjon med gradert uttak V8`() {
        val spec = impersonalGradertUttakSpec()
        `when`(simuleringService.simulerAlderspensjon(spec))
            .thenReturn(simuleringsresultat(spec.simuleringType, heltUttak = false))
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(gradertUttakRequestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyForGradertUttakV8()))
    }

    @Test
    fun `simulerer alderspensjon med privat AFP V8`() {
        val spec = impersonalHeltUttakSpec(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT, epsHarInntektOver2G = true)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(heltUttakRequestBodyV8(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyMedPrivatAfpV8()))
    }

    @Test
    fun `simulerer alderspensjon med livsvarig offentlig AFP V8`() {
        val spec = impersonalHeltUttakSpec(
            SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG,
            epsHarInntektOver2G = true
        )
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(heltUttakRequestBodyV8(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyMedLivsvarigOffentligAfpV8()))
    }

    @Test
    fun `simulering responds 'vilkaar ikke oppfylt' when Conflict V8`() {
        val spec = impersonalHeltUttakSpec(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT, epsHarInntektOver2G = true)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenThrow(conflict())
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(heltUttakRequestBodyV8(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(VILKAAR_IKKE_OPPFYLT_RESPONSE_BODY))
    }

    @Test
    fun `simulerer endring av alderspensjon V8`() {
        val spec = impersonalGradertUttakSpec(SimuleringType.ENDRING_ALDERSPENSJON)
        `when`(simuleringService.simulerAlderspensjon(spec))
            .thenReturn(simuleringsresultat(spec.simuleringType, heltUttak = false))
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(gradertUttakRequestBody(SimuleringType.ENDRING_ALDERSPENSJON))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyForGradertUttakV8()))
    }

    @Test
    fun `simulerer endring av alderspensjon med privat AFP V8`() {
        val spec = impersonalGradertUttakSpec(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec))
            .thenReturn(simuleringsresultat(spec.simuleringType, heltUttak = false))
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(gradertUttakRequestBody(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyForGradertUttakV8()))
    }

    @Test
    fun `simulerer endring av alderspensjon med offentlig livsvarig AFP V8`() {
        val spec = impersonalGradertUttakSpec(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG)
        `when`(simuleringService.simulerAlderspensjon(spec))
            .thenReturn(simuleringsresultat(spec.simuleringType, heltUttak = false))
        enableUtvidetResult()

        mvc.perform(
            post(URL_V8)
                .with(csrf())
                .content(gradertUttakRequestBody(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyMedLivsvarigOffentligAfpV8()))
    }

    private fun enableUtvidetResult() {
        `when`(feature.isEnabled("utvidet-simuleringsresultat")).thenReturn(true)
    }

    private companion object {

        private const val URL_V8 = "/api/v8/alderspensjon/simulering"
        private const val PENSJONSBELOEP = 123456

        @Language("json")
        private fun heltUttakRequestBodyV8(simuleringType: SimuleringType) = """{
            "simuleringstype": "$simuleringType",
            "foedselsdato": "1963-12-31",
            "aarligInntektFoerUttakBeloep": 100000,
            "heltUttak": {
               "uttaksalder": { "aar": 67, "maaneder": 1 },
               "aarligInntektVsaPensjon": {
                  "beloep": 50000,
                  "sluttAlder": { "aar": 75, "maaneder": 0 } }
            },
            "utenlandsperiodeListe": [{
              "fom": "1990-01-02",
              "tom": "1999-11-30",
              "landkode": "AUS",
              "arbeidetUtenlands": true
            }],
            "sivilstand": "UGIFT",
            "epsHarInntektOver2G": true,
            "epsHarPensjon": false
        }""".trimIndent()

        @Language("json")
        private fun gradertUttakRequestBody(simuleringType: SimuleringType = SimuleringType.ALDERSPENSJON) = """{
            "simuleringstype": "$simuleringType",
            "foedselsdato": "1963-12-31",
            "epsHarInntektOver2G": true,
            "aarligInntektFoerUttakBeloep": 100000,
            "sivilstand": "SAMBOER",
            "gradertUttak": {
               "grad": 40,
               "uttaksalder": { "aar": 62, "maaneder": 9 },
               "aarligInntektVsaPensjonBeloep": 75000
            },
            "heltUttak": {
               "uttaksalder": { "aar": 67, "maaneder": 1 },
               "aarligInntektVsaPensjon": {
                   "beloep": 50000,
                   "sluttAlder": { "aar": 75, "maaneder": 0 } }
            }
        }""".trimIndent()

        private fun impersonalHeltUttakSpec(simuleringType: SimuleringType, epsHarInntektOver2G: Boolean = false) =
            ImpersonalSimuleringSpec(
                simuleringType = simuleringType,
                eps = Eps(harInntektOver2G = epsHarInntektOver2G, harPensjon = false),
                forventetAarligInntektFoerUttak = 100_000,
                sivilstand = Sivilstand.UGIFT,
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(67, 1),
                    inntekt = Inntekt(50_000, Alder(75, 0))
                ),
                utenlandsopphold = Utenlandsopphold(
                    periodeListe = listOf(
                        Opphold(
                            fom = LocalDate.of(1990, 1, 2),
                            tom = LocalDate.of(1999, 11, 30),
                            land = Land.AUS,
                            arbeidet = true
                        )
                    )
                )
            )

        private fun impersonalGradertUttakSpec(simuleringType: SimuleringType = SimuleringType.ALDERSPENSJON) =
            ImpersonalSimuleringSpec(
                simuleringType = simuleringType,
                eps = Eps(harInntektOver2G = true, harPensjon = false),
                forventetAarligInntektFoerUttak = 100_000,
                sivilstand = Sivilstand.SAMBOER,
                gradertUttak = GradertUttak(
                    grad = Uttaksgrad.FOERTI_PROSENT,
                    uttakFomAlder = Alder(62, 9),
                    aarligInntekt = 75_000
                ),
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(67, 1),
                    inntekt = Inntekt(50_000, Alder(75, 0))
                ),
                utenlandsopphold = Utenlandsopphold(periodeListe = emptyList())
            )

        @Language("json")
        private fun responseBodyForHeltUttakV8() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "alderspensjonMaanedligVedEndring":
                {
                    "heltUttakMaanedligBeloep": 0
                }
            }""".trimIndent()

        @Language("json")
        private fun responseBodyForGradertUttakV8() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "alderspensjonMaanedligVedEndring":
                {
                    "gradertUttakMaanedligBeloep": 0,
                    "heltUttakMaanedligBeloep": 0
                }
            }""".trimIndent()

        @Language("json")
        private fun responseBodyMedPrivatAfpV8() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "alderspensjonMaanedligVedEndring":
                {
                    "heltUttakMaanedligBeloep": 0
                },
            "afpPrivat": 
              [
                {
                  "beloep": 22056,
                  "alder": 67
                }
              ]
            }""".trimIndent()

        @Language("json")
        private fun responseBodyMedLivsvarigOffentligAfpV8() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "alderspensjonMaanedligVedEndring":
                {
                    "heltUttakMaanedligBeloep": 0
                },
            "afpOffentlig": [
                  {
                    "beloep": 22056,
                    "alder": 67
                  }
                ]
              }
              """.trimIndent()

        @Language("json")
        private const val VILKAAR_IKKE_OPPFYLT_RESPONSE_BODY =
            """{"alderspensjon":[],"vilkaarsproeving":{"vilkaarErOppfylt":false}}"""

        private fun simuleringsresultat(simuleringType: SimuleringType, heltUttak: Boolean = true) =
            when (simuleringType) {
                SimuleringType.ALDERSPENSJON -> SimuleringResult(
                    alderspensjon = listOf(
                        SimulertAlderspensjon(
                            alder = 67,
                            beloep = PENSJONSBELOEP,
                            inntektspensjonBeloep = 0,
                            garantipensjonBeloep = 0,
                            delingstall = 0.0,
                            pensjonBeholdningFoerUttak = 0
                        )
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                        gradertUttak = if (heltUttak) null else 0,
                        heltUttak = 0
                    ),
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT -> SimuleringResult(
                    alderspensjon = listOf(
                        SimulertAlderspensjon(
                            alder = 67,
                            beloep = PENSJONSBELOEP,
                            inntektspensjonBeloep = 0,
                            garantipensjonBeloep = 0,
                            delingstall = 0.0,
                            pensjonBeholdningFoerUttak = 0
                        )
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                        gradertUttak = if (heltUttak) null else 0,
                        heltUttak = 0
                    ),
                    afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 22056)),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG -> SimuleringResult(
                    alderspensjon = listOf(
                        SimulertAlderspensjon(
                            alder = 67,
                            beloep = PENSJONSBELOEP,
                            inntektspensjonBeloep = 0,
                            garantipensjonBeloep = 0,
                            delingstall = 0.0,
                            pensjonBeholdningFoerUttak = 0
                        )
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                        gradertUttak = if (heltUttak) null else 0,
                        heltUttak = 0
                    ),
                    afpPrivat = emptyList(),
                    afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 22056)),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ENDRING_ALDERSPENSJON -> SimuleringResult(
                    alderspensjon = listOf(
                        SimulertAlderspensjon(
                            alder = 67,
                            beloep = PENSJONSBELOEP,
                            inntektspensjonBeloep = 0,
                            garantipensjonBeloep = 0,
                            delingstall = 0.0,
                            pensjonBeholdningFoerUttak = 0
                        )
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                        gradertUttak = if (heltUttak) null else 0,
                        heltUttak = 0
                    ),
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT -> SimuleringResult(
                    alderspensjon = listOf(
                        SimulertAlderspensjon(
                            alder = 67,
                            beloep = PENSJONSBELOEP,
                            inntektspensjonBeloep = 0,
                            garantipensjonBeloep = 0,
                            delingstall = 0.0,
                            pensjonBeholdningFoerUttak = 0
                        )
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                        gradertUttak = if (heltUttak) null else 0,
                        heltUttak = 0
                    ),
                    afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 22056)),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG -> SimuleringResult(
                    alderspensjon = listOf(
                        SimulertAlderspensjon(
                            alder = 67,
                            beloep = PENSJONSBELOEP,
                            inntektspensjonBeloep = 0,
                            garantipensjonBeloep = 0,
                            delingstall = 0.0,
                            pensjonBeholdningFoerUttak = 0
                        )
                    ),
                    alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
                        gradertUttak = if (heltUttak) null else 0,
                        heltUttak = 0
                    ),
                    afpPrivat = emptyList(),
                    afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 22056)),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )
            }

        private fun conflict() = EgressException(message = "", statusCode = HttpStatus.CONFLICT)
    }
}
