package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import no.nav.pensjon.kalkulator.testutil.Assertions.assertAlder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SimuleringMapperV3Test {

    @Test
    fun `resultatV3 maps domain object to data transfer object`() {
        val dto = SimuleringMapperV3.resultatV3(
            Simuleringsresultat(
                alderspensjon = listOf(
                    SimulertAlderspensjon(alder = 67, beloep = 1001),
                    SimulertAlderspensjon(alder = 68, beloep = 1002)
                ),
                afpPrivat = listOf(
                    SimulertAfpPrivat(alder = 62, beloep = 2001),
                    SimulertAfpPrivat(alder = 63, beloep = 2002)
                ),
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = false,
                    alternativ = Alternativ(
                        uttakGrad = Uttaksgrad.HUNDRE_PROSENT,
                        heltUttakAlder = Alder(aar = 69, maaneder = 7)
                    )
                )
            )
        )

        with(dto.alderspensjon) {
            assertPensjon(expectedAlderAar = 67, expectedBeloep = 1001, actualPensjon = this[0])
            assertPensjon(expectedAlderAar = 68, expectedBeloep = 1002, actualPensjon = this[1])
        }
        with(dto.afpPrivat) {
            assertPensjon(expectedAlderAar = 62, expectedBeloep = 2001, actualPensjon = this[0])
            assertPensjon(expectedAlderAar = 63, expectedBeloep = 2002, actualPensjon = this[1])
        }
        with(dto.vilkaarsproeving) {
            assertFalse(vilkaarErOppfylt)
            assertNull(alternativ?.uttaksgrad) // 100 % is mapped to null
        }
    }

    @Test
    fun `fromIngressSimuleringSpecV3 maps data transfer object to domain object`() {
        val spec: ImpersonalSimuleringSpec = SimuleringMapperV3.fromIngressSimuleringSpecV3(
            IngressSimuleringSpecV3(
                simuleringstype = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
                foedselsdato = LocalDate.of(1969, 3, 2),
                epsHarInntektOver2G = true,
                aarligInntektFoerUttakBeloep = 123_000,
                sivilstand = Sivilstand.REGISTRERT_PARTNER,
                gradertUttak = IngressSimuleringGradertUttakV3(
                    grad = 40,
                    uttaksalder = IngressSimuleringAlderV3(aar = 68, maaneder = 2),
                    aarligInntektVsaPensjonBeloep = 234_000
                ),
                heltUttak = IngressSimuleringHeltUttakV3(
                    uttaksalder = IngressSimuleringAlderV3(aar = 70, maaneder = 4),
                    aarligInntektVsaPensjon = IngressSimuleringInntektV3(
                        beloep = 1_000,
                        sluttAlder = IngressSimuleringAlderV3(aar = 75, maaneder = 0)
                    ),
                )
            )
        )

        with(spec) {
            assertEquals(SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT, simuleringType)
            assertTrue(epsHarInntektOver2G)
            assertEquals(123_000, forventetAarligInntektFoerUttak)
            assertEquals(Sivilstand.REGISTRERT_PARTNER, sivilstand)
            with(gradertUttak!!) {
                assertEquals(Uttaksgrad.FOERTI_PROSENT, grad)
                assertEquals(234_000, aarligInntekt)
                assertAlder(expectedAar = 68, expectedMaaneder = 2, actualAlder = uttakFomAlder)
            }
            with(heltUttak) {
                assertAlder(expectedAar = 70, expectedMaaneder = 4, actualAlder = uttakFomAlder!!)
                with(inntekt!!) {
                    assertEquals(1_000, aarligBeloep)
                    assertAlder(expectedAar = 75, expectedMaaneder = 0, actualAlder = tomAlder)
                }
            }
        }
    }

    private companion object {
        private fun assertPensjon(expectedAlderAar: Int, expectedBeloep: Int, actualPensjon: PensjonsberegningV3) {
            with(actualPensjon) {
                assertEquals(expectedAlderAar, alder)
                assertEquals(expectedBeloep, beloep)
            }
        }
    }
}
