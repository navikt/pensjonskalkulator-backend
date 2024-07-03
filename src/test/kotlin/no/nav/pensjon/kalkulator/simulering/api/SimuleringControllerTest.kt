package no.nav.pensjon.kalkulator.simulering.api

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
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
class SimuleringControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var simuleringService: SimuleringService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun `simulerer hel alderspensjon V4`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V4)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV4(SimuleringType.ALDERSPENSJON)))
    }


    @Test
    fun `simulerer hel alderspensjon V5`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V5)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV5()))
    }

    @Test
    fun `simulerer hel alderspensjon V6`() {
        val spec = impersonalHeltUttakSpecV6(SimuleringType.ALDERSPENSJON)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V6)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV6()))
    }

    @Test
    fun `simulerer alderspensjon med gradert uttak V4`() {
        val spec = impersonalGradertUttakSpec()
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V4)
                .with(csrf())
                .content(gradertUttakRequestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV4(SimuleringType.ALDERSPENSJON)))
    }

    @Test
    fun `simulerer alderspensjon med gradert uttak V5`() {
        val spec = impersonalGradertUttakSpec()
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V5)
                .with(csrf())
                .content(gradertUttakRequestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV5()))
    }

    @Test
    fun `simulerer alderspensjon med gradert uttak V6`() {
        val spec = impersonalGradertUttakSpec()
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V6)
                .with(csrf())
                .content(gradertUttakRequestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV6()))
    }


    @Test
    fun `simulerer alderspensjon med AFP privat V4`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V4)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV4(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)))
    }

    @Test
    fun `simulerer alderspensjon med AFP privat V5`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V5)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV5MedAfpPrivat()))
    }


    @Test
    fun `simulerer alderspensjon med AFP privat V6`() {
        val spec = impersonalHeltUttakSpecV6(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V6)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV6MedAfpPrivat()))
    }

    @Test
    fun `simulerer alderspensjon med AFP offentlig V4`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V4)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV4MedAFPOffentlig()))
    }

    @Test
    fun `simulerer alderspensjon med AFP offentlig V5`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V5)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV5MedAFPOffentlig()))
    }

    @Test
    fun `simulerer alderspensjon med AFP offentlig V6`() {
        val spec = impersonalHeltUttakSpecV6(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenReturn(simuleringsresultat(spec.simuleringType))

        mvc.perform(
            post(URL_V6)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(responseBodyV6MedAFPOffentlig()))
    }


    @Test
    fun `simulering responds 'vilkaar ikke oppfylt' when Conflict V4`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenThrow(conflict())

        mvc.perform(
            post(URL_V4)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(SimuleringController.VILKAAR_IKKE_OPPFYLT_EXAMPLE))
    }

    @Test
    fun `simulering responds 'vilkaar ikke oppfylt' when Conflict V5`() {
        val spec = impersonalHeltUttakSpecV1to5(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenThrow(conflict())

        mvc.perform(
            post(URL_V5)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(SimuleringController.VILKAAR_IKKE_OPPFYLT_EXAMPLE_V5))
    }

    @Test
    fun `simulering responds 'vilkaar ikke oppfylt' when Conflict V6`() {
        val spec = impersonalHeltUttakSpecV6(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        `when`(simuleringService.simulerAlderspensjon(spec)).thenThrow(conflict())

        mvc.perform(
            post(URL_V6)
                .with(csrf())
                .content(heltUttakRequestBody(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(SimuleringController.VILKAAR_IKKE_OPPFYLT_EXAMPLE_V6))
    }


    private companion object {

        private const val URL_V4 = "/api/v4/alderspensjon/simulering"
        private const val URL_V5 = "/api/v5/alderspensjon/simulering"
        private const val URL_V6 = "/api/v6/alderspensjon/simulering"
        private const val PENSJONSBELOEP = 123456

        @Language("json")
        private fun heltUttakRequestBody(simuleringType: SimuleringType) = """{
            "simuleringstype": "$simuleringType",
            "foedselsdato": "1963-12-31",
            "epsHarInntektOver2G": false,
            "aarligInntektFoerUttakBeloep": 100000,
            "sivilstand": "UGIFT",
            "heltUttak": {
               "uttaksalder": { "aar": 67, "maaneder": 1 },
               "aarligInntektVsaPensjon": {
                  "beloep": 50000,
                  "sluttAlder": { "aar": 75, "maaneder": 0 } }
            },
            "utenlandsperiodeListe": [{
              "fom": "1990-01-02",
              "tom": "1999-11-30",
              "land": "AUS",
              "arbeidetUtenlands": true
            }]
        }""".trimIndent()

        @Language("json")
        private fun gradertUttakRequestBody() = """{
            "simuleringstype": "ALDERSPENSJON",
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

        private fun impersonalHeltUttakSpecV6(simuleringType: SimuleringType) =
            ImpersonalSimuleringSpec(
                simuleringType = simuleringType,
                epsHarInntektOver2G = false,
                forventetAarligInntektFoerUttak = 100_000,
                sivilstand = Sivilstand.UGIFT,
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(67, 1),
                    inntekt = Inntekt(50_000, Alder(75, 0))
                ),
                utenlandsperiodeListe = listOf(
                    UtenlandsperiodeSpec(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        land = "AUS",
                        arbeidetUtenlands = true
                    )
                )
            )

        private fun impersonalHeltUttakSpecV1to5(simuleringType: SimuleringType) =
            ImpersonalSimuleringSpec(
                simuleringType = simuleringType,
                epsHarInntektOver2G = false,
                forventetAarligInntektFoerUttak = 100_000,
                sivilstand = Sivilstand.UGIFT,
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(67, 1),
                    inntekt = Inntekt(50_000, Alder(75, 0))
                ),
                utenlandsperiodeListe = emptyList()
            )

        private fun impersonalGradertUttakSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                epsHarInntektOver2G = true,
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
                utenlandsperiodeListe = emptyList()
            )

        @Language("json")
        private fun responseBodyV5() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ]
            }""".trimIndent()

        @Language("json")
        private fun responseBodyV6() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ]
            }""".trimIndent()

        @Language("json")
        private fun responseBodyV4(simuleringstype: SimuleringType) = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "afpPrivat": ${
            when (simuleringstype) {
                SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG -> "[]"
                SimuleringType.ALDERSPENSJON -> "[]"
                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT -> """
              [
                {
                  "beloep": 22056,
                  "alder": 67
                }
              ]
              """
            }
        }}""".trimIndent()

        @Language("json")
        private fun responseBodyV5MedAfpPrivat() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "afpPrivat": {
              "afpPrivatListe":
              [
                {
                  "beloep": 22056,
                  "alder": 67
                }
              ]
              }
            }
        }""".trimIndent()

        @Language("json")
        private fun responseBodyV6MedAfpPrivat() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "afpPrivat": 
              [
                {
                  "beloep": 22056,
                  "alder": 67
                }
              ]
            }
        }""".trimIndent()

        @Language("json")
        private fun responseBodyV4MedAFPOffentlig() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "afpPrivat": [],
            "afpOffentlig": {
              "afpLeverandoer": "Statens pensjonskasse",
              "afpOffentligListe": [
                  {
                    "beloep": 22056,
                    "alder": 67
                  }
                ]
              }
              }
              """.trimIndent()

        @Language("json")
        private fun responseBodyV5MedAFPOffentlig() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "afpOffentlig": {
              "afpLeverandoer": "Statens pensjonskasse",
              "afpOffentligListe": [
                  {
                    "beloep": 22056,
                    "alder": 67
                  }
                ]
              }
              }
              """.trimIndent()


        @Language("json")
        private fun responseBodyV6MedAFPOffentlig() = """{
            "alderspensjon": [
              {
                "beloep": $PENSJONSBELOEP,
                "alder": 67
              }
            ],
            "afpOffentlig": [
                  {
                    "beloep": 22056,
                    "alder": 67
                  }
                ]
              }
              """.trimIndent()

        private fun simuleringsresultat(simuleringType: SimuleringType) =
            when (simuleringType) {
                SimuleringType.ALDERSPENSJON -> Simuleringsresultat(
                    alderspensjon = listOf(SimulertAlderspensjon(alder = 67, beloep = PENSJONSBELOEP)),
                    afpPrivat = emptyList(),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null)
                )

                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT -> Simuleringsresultat(
                    alderspensjon = listOf(SimulertAlderspensjon(alder = 67, beloep = PENSJONSBELOEP)),
                    afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 22056)),
                    afpOffentlig = emptyList(),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null)
                )

                SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG -> Simuleringsresultat(
                    alderspensjon = listOf(SimulertAlderspensjon(alder = 67, beloep = PENSJONSBELOEP)),
                    afpPrivat = emptyList(),
                    afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 22056, afpLeverandoer = "Statens pensjonskasse")),
                    vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null)
                )
            }

        private fun conflict() = EgressException(message = "", statusCode = HttpStatus.CONFLICT)
    }
}
