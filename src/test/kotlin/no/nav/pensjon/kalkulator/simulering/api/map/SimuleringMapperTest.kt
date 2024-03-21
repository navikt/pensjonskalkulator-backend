package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SimuleringMapperTest {

    @Test
    fun `resultatDto maps domain object to data transfer object`() {
        val dto = SimuleringMapper.resultatDto(
            Simuleringsresultat(
                alderspensjon = listOf(
                    SimulertAlderspensjon(alder = 67, beloep = 1001),
                    SimulertAlderspensjon(alder = 68, beloep = 1002)
                ),
                afpPrivat = listOf(
                    SimulertAfpPrivat(alder = 62, beloep = 2001),
                    SimulertAfpPrivat(alder = 63, beloep = 2002)
                ),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null)
            )
        )

        with(dto.alderspensjon[0]) {
            assertEquals(67, alder)
            assertEquals(1001, beloep)
        }
        with(dto.alderspensjon[1]) {
            assertEquals(68, alder)
            assertEquals(1002, beloep)
        }
        with(dto.afpPrivat[0]) {
            assertEquals(62, alder)
            assertEquals(2001, beloep)
        }
        with(dto.afpPrivat[1]) {
            assertEquals(63, alder)
            assertEquals(2002, beloep)
        }
        assertTrue(dto.vilkaarErOppfylt)
    }

    @Test
    fun `fromIngressSimuleringSpecV2 maps data transfer object to domain object`() {
        val spec: ImpersonalSimuleringSpec = SimuleringMapper.fromIngressSimuleringSpecV2(
            IngressSimuleringSpecV2(
                simuleringstype = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
                foedselsdato = LocalDate.of(1969, 3, 2),
                epsHarInntektOver2G = true,
                aarligInntektFoerUttakBeloep = 123_000,
                sivilstand = Sivilstand.REGISTRERT_PARTNER,
                gradertUttak = IngressSimuleringGradertUttakV2(
                    grad = 40,
                    uttaksalder = IngressSimuleringAlderV2(aar = 68, maaneder = 2),
                    aarligInntektVsaPensjonBeloep = 234_000
                ),
                heltUttak = IngressSimuleringHeltUttakV2(
                    uttaksalder = IngressSimuleringAlderV2(70, 4),
                    aarligInntektVsaPensjon = IngressSimuleringInntektV2(
                        beloep = 1_000,
                        sluttAlder = IngressSimuleringAlderV2(aar = 75, maaneder = 0)
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
                with(uttakFomAlder) {
                    assertEquals(68, aar)
                    assertEquals(2, maaneder)
                }
            }
            with(heltUttak) {
                assertEquals(Alder(70, 4), uttakFomAlder)
                with(inntekt!!) {
                    assertEquals(1_000, aarligBeloep)
                    with(tomAlder) {
                        assertEquals(75, aar)
                        assertEquals(0, maaneder)
                    }
                }
            }
        }
    }
}
