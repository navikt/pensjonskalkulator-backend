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
                    SimulertAlderspensjon(67, 1001),
                    SimulertAlderspensjon(68, 1002)
                ),
                afpPrivat = listOf(
                    SimulertAfpPrivat(62, 2001),
                    SimulertAfpPrivat(63, 2002)
                )
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
    fun `fromIngressSpecDto maps data transfer object to domain object`() {
        val spec: ImpersonalSimuleringSpec = SimuleringMapper.fromIngressSpecDtoV2(
            SimuleringIngressSpecDtoV2(
                simuleringstype = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
                foedselsdato = LocalDate.of(1969, 3, 2),
                epsHarInntektOver2G = true,
                forventetInntekt = 123_000,
                sivilstand = Sivilstand.REGISTRERT_PARTNER,
                gradertUttak = SimuleringGradertUttakIngressDtoV2(
                    grad = 40,
                    uttaksalder = SimuleringAlderDto(aar = 68, maaneder = 2),
                    aarligInntekt = 234_000
                ),
                heltUttak = SimuleringHeltUttakIngressDtoV2(
                    uttaksalder = SimuleringAlderDto(70, 4),
                    aarligInntektVsaPensjon = SimuleringInntektDtoV2(
                        beloep = 1_000,
                        sluttalder = SimuleringAlderDto(aar = 75, maaneder = 0)
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

                with(uttakFomAlder) {
                    assertEquals(68, aar)
                    assertEquals(2, maaneder)
                }
            }

            with(heltUttak) {
                with(uttakFomAlder) {
                    assertEquals(70, aar)
                    assertEquals(4, maaneder)
                }

                with(inntekt!!) {
                    assertEquals(1_000, aarligBeloep)

                    with(tomAlder) {
                        assertEquals(75, aar)
                        assertEquals(0, maaneder)
                    }
                }

                assertEquals(Alder(70, 4), uttakFomAlder)
            }
        }
    }
}
