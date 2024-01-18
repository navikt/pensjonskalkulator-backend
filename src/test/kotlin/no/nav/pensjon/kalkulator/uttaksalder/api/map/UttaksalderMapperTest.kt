package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class UttaksalderMapperTest {

    @Test
    fun `toDto maps domain object to data transfer object`() {
        val dto = UttaksalderMapper.toDto(Alder(2024, 5))

        with(dto!!) {
            assertEquals(2024, aar)
            assertEquals(5, maaneder)
        }
    }

    @Test
    fun `fromIngressSpecDto maps data transfer object to domain object`() {
        val spec = UttaksalderMapper.fromIngressSpecDto(
            UttaksalderIngressSpecDto(
                sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
                harEps = true,
                sisteInntekt = 123,
                simuleringstype = SimuleringType.ALDERSPENSJON,
                gradertUttak = UttaksalderGradertUttakIngressDto(
                    grad = 50,
                    aarligInntektVsaPensjon = 456,
                    heltUttakAlder = UttaksalderAlderDto(70, 2),
                    foedselsdato = LocalDate.of(1964, 5, 6)
                )
            )
        )

        with(spec) {
            assertEquals(Sivilstand.GJENLEVENDE_PARTNER, sivilstand)
            assertTrue(harEps!!)
            assertEquals(123, aarligInntektFoerUttak)
            assertEquals(SimuleringType.ALDERSPENSJON, simuleringType)

            with(gradertUttak!!) {
                assertEquals(Uttaksgrad.FEMTI_PROSENT, grad)
                assertEquals(456, aarligInntekt)
                assertEquals(LocalDate.of(1964, 5, 6), foedselDato)
            }
        }
    }

    @Test
    fun `fromIngressSpecDtoV2 maps data transfer object to domain object`() {
        val dto = UttaksalderIngressSpecDtoV2(
            sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
            harEps = true,
            aarligInntekt = 123,
            simuleringstype = SimuleringType.ALDERSPENSJON,
            gradertUttak = UttaksalderGradertUttakIngressDtoV2(
                grad = 50,
                aarligInntekt = 456
            ),
            heltUttak = UttaksalderHeltUttakIngressDtoV2(
                uttaksalder = UttaksalderAlderDto(70, 2),
                aarligInntektVsaPensjon = UttaksalderInntektDtoV2(
                    beloep = 456,
                    sluttAlder = UttaksalderAlderDto(72, 5)
                )
            )
        )

        val spec = UttaksalderMapper.fromIngressSpecDtoV2(dto)

        with(spec) {
            assertEquals(Sivilstand.GJENLEVENDE_PARTNER, sivilstand)
            assertTrue(harEps!!)
            assertEquals(123, aarligInntektFoerUttak)
            assertEquals(SimuleringType.ALDERSPENSJON, simuleringType)

            with(gradertUttak!!) {
                assertEquals(Uttaksgrad.FEMTI_PROSENT, grad)
                assertEquals(456, aarligInntekt)
                assertEquals(LocalDate.MIN, foedselDato)
            }

            with(heltUttak) {
                with(uttakFomAlder!!) {
                    assertEquals(70, aar)
                    assertEquals(2, maaneder)
                }

                with(inntekt!!) {
                    assertEquals(456, aarligBeloep)

                    with(tomAlder) {
                        assertEquals(72, aar)
                        assertEquals(5, maaneder)
                    }
                }
            }
        }
    }
}
