package no.nav.pensjon.kalkulator.simulering.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(SimuleringController::class)
@Import(MockSecurityConfiguration::class)
class SimuleringControllerTest : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var simuleringService: SimuleringService

    @MockkBean
    private lateinit var feature: FeatureToggleService

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

        should("simulere hel alderspensjon V9") {
            val spec = impersonalHeltUttakSpec(SimuleringType.ALDERSPENSJON)
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } returns simuleringsresultat(spec.simuleringType)
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(heltUttakRequestBodyV9(SimuleringType.ALDERSPENSJON))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBodyForHeltUttakV9()))
        }

        should("simulere alderspensjon med gradert uttak V9") {
            val spec = impersonalGradertUttakSpec()
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } returns simuleringsresultat(spec.simuleringType, heltUttak = false)
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(gradertUttakRequestBody())
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBodyForGradertUttakV9()))
        }

        should("simulere alderspensjon med privat AFP V9") {
            val spec = impersonalHeltUttakSpec(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } returns simuleringsresultat(spec.simuleringType)
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(heltUttakRequestBodyV9(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBodyMedPrivatAfpV9()))
        }

        should("simulere alderspensjon med livsvarig offentlig AFP V9") {
            val spec = impersonalHeltUttakSpec(
                SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG
            )
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } returns simuleringsresultat(spec.simuleringType)
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(heltUttakRequestBodyV9(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBodyMedLivsvarigOffentligAfpV9()))
        }

        should("respondere med 'vilkaar ikke oppfylt' ved konflikt V9") {
            val spec = impersonalHeltUttakSpec(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } throws conflict()
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(heltUttakRequestBodyV9(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(VILKAAR_IKKE_OPPFYLT_RESPONSE_BODY))
        }

        should("simulere endring av alderspensjon V9") {
            val spec = impersonalGradertUttakSpec(SimuleringType.ENDRING_ALDERSPENSJON)
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } returns simuleringsresultat(spec.simuleringType, heltUttak = false)
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(gradertUttakRequestBody(SimuleringType.ENDRING_ALDERSPENSJON))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBodyForGradertUttakV9()))
        }

        should("simulere endring av alderspensjon med privat AFP V9") {
            val spec = impersonalGradertUttakSpec(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT)
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } returns simuleringsresultat(spec.simuleringType, heltUttak = false)
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(gradertUttakRequestBody(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBodyForGradertUttakV9()))
        }

        should("simulere endring av alderspensjon med offentlig livsvarig AFP V9") {
            val spec = impersonalGradertUttakSpec(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG)
            every {
                simuleringService.simulerPersonligAlderspensjon(spec)
            } returns simuleringsresultat(spec.simuleringType, heltUttak = false)
            enableUtvidetResult()

            mvc.perform(
                post(URL_V9)
                    .with(csrf())
                    .content(gradertUttakRequestBody(SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBodyMedLivsvarigOffentligAfpV9()))
        }
    }

    private fun enableUtvidetResult() {
        every { feature.isEnabled("utvidet-simuleringsresultat") } returns true
    }

    private companion object {

        private const val URL_V9 = "/api/v9/alderspensjon/simulering"
        private const val PENSJONSBELOEP = 123456

        @Language("json")
        private fun heltUttakRequestBodyV9(simuleringType: SimuleringType) = """{
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

        private fun impersonalHeltUttakSpec(simuleringType: SimuleringType) =
            ImpersonalSimuleringSpec(
                simuleringType = simuleringType,
                eps = Eps(harInntektOver2G = true, harPensjon = false),
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
        private fun responseBodyForHeltUttakV9() = """{
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
        private fun responseBodyForGradertUttakV9() = """{
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
        private fun responseBodyMedPrivatAfpV9() = """{
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
        private fun responseBodyMedLivsvarigOffentligAfpV9() = """{
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
                SimuleringType.ALDERSPENSJON,
                SimuleringType.ALDERSPENSJON_MED_GJENLEVENDERETT -> SimuleringResult(
                    alderspensjon = listOf(alderspensjon()),
                    alderspensjonMaanedsbeloep = maanedsbeloep(heltUttak),
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproeving(),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.PRE2025_OFFENTLIG_AFP_ETTERFULGT_AV_ALDERSPENSJON -> SimuleringResult(
                    alderspensjon = listOf(alderspensjon()),
                    alderspensjonMaanedsbeloep = maanedsbeloep(heltUttak),
                    afpPrivat = listOf(privatAfp()),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproeving(),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT -> SimuleringResult(
                    alderspensjon = listOf(alderspensjon()),
                    alderspensjonMaanedsbeloep = maanedsbeloep(heltUttak),
                    afpPrivat = listOf(privatAfp()),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproeving(),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG -> SimuleringResult(
                    alderspensjon = listOf(alderspensjon()),
                    alderspensjonMaanedsbeloep = maanedsbeloep(heltUttak),
                    afpPrivat = emptyList(),
                    afpOffentlig = listOf(livsvarigOffentligAfp()),
                    vilkaarsproeving = vilkaarsproeving(),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ENDRING_ALDERSPENSJON,
                SimuleringType.ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT -> SimuleringResult(
                    alderspensjon = listOf(alderspensjon()),
                    alderspensjonMaanedsbeloep = maanedsbeloep(heltUttak),
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproeving(),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT -> SimuleringResult(
                    alderspensjon = listOf(alderspensjon()),
                    alderspensjonMaanedsbeloep = maanedsbeloep(heltUttak),
                    afpPrivat = listOf(privatAfp()),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = vilkaarsproeving(),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )

                SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG -> SimuleringResult(
                    alderspensjon = listOf(alderspensjon()),
                    alderspensjonMaanedsbeloep = maanedsbeloep(heltUttak),
                    afpPrivat = emptyList(),
                    afpOffentlig = listOf(livsvarigOffentligAfp()),
                    vilkaarsproeving = vilkaarsproeving(),
                    harForLiteTrygdetid = false,
                    trygdetid = 0,
                    opptjeningGrunnlagListe = emptyList()
                )
            }

        private fun alderspensjon() =
            SimulertAlderspensjon(
                alder = 67,
                beloep = PENSJONSBELOEP,
                inntektspensjonBeloep = 0,
                garantipensjonBeloep = 0,
                delingstall = 0.0,
                pensjonBeholdningFoerUttak = 0,
                andelsbroekKap19 = 0.0,
                andelsbroekKap20 = 0.0,
                sluttpoengtall = 0.0,
                trygdetidKap19 = 0,
                trygdetidKap20 = 0,
                poengaarFoer92 = 0,
                poengaarEtter91 = 0,
                forholdstall = 0.0,
                grunnpensjon = 0,
                tilleggspensjon = 0,
                pensjonstillegg = 0,
                skjermingstillegg = 0,
                kapittel19Gjenlevendetillegg = 0
            )

        private fun maanedsbeloep(heltUttak: Boolean) =
            AlderspensjonMaanedsbeloep(
                gradertUttak = if (heltUttak) null else 0,
                heltUttak = 0
            )

        private fun livsvarigOffentligAfp() =
            SimulertAfpOffentlig(alder = 67, beloep = 22056, maanedligBeloep = 1900)

        private fun privatAfp() =
            SimulertAfpPrivat(
                alder = 67,
                beloep = 22056,
                kompensasjonstillegg = 123,
                kronetillegg = 5,
                livsvarig = 93,
                maanedligBeloep = 1900
            )

        private fun vilkaarsproeving() =
            Vilkaarsproeving(innvilget = true, alternativ = null)

        private fun conflict() =
            EgressException(message = "", statusCode = HttpStatus.CONFLICT)
    }
}
