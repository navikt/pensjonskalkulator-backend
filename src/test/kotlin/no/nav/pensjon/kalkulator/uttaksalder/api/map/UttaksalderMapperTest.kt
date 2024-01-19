package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
    fun `fromIngressSpecForGradertUttakV1 maps data transfer object to domain object`() {
        val dto = IngressUttaksalderSpecForGradertUttakV1(
            simuleringstype = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
            harEps = true,
            aarligInntektFoerUttakBeloep = 100_000,
            gradertUttak = IngressUttaksalderGradertUttakV1(
                grad = 50,
                aarligInntektVsaPensjonBeloep = 50_000
            ),
            heltUttak = IngressUttaksalderHeltUttakV1(
                uttaksalder = IngressUttaksalderAlderV1(aar = 67, maaneder = 1),
                aarligInntektVsaPensjon = IngressUttaksalderInntektV1(
                    beloep = 25_000,
                    sluttAlder = IngressUttaksalderAlderV1(aar = 70, maaneder = 11)
                )
            )
        )

        val actual = UttaksalderMapper.fromIngressSpecForGradertUttakV1(dto)

        val expected = ImpersonalUttaksalderSpec(
            sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            gradertUttak = UttaksalderGradertUttak(
                grad = Uttaksgrad.FEMTI_PROSENT,
                aarligInntekt = 50_000,
                foedselDato = LocalDate.MIN // deprecated; irrelevant
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 67, maaneder = 1),
                inntekt = Inntekt(
                    aarligBeloep = 25_000,
                    tomAlder = Alder(aar = 70, maaneder = 11)
                )
            )
        )

        actual shouldBe expected
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
                uttaksalder = UttaksalderAlderDto(aar = 70, maaneder = 2),
                aarligInntektVsaPensjon = UttaksalderInntektDtoV2(
                    beloep = 456,
                    sluttAlder = UttaksalderAlderDto(aar = 72, maaneder = 5)
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

            with(heltUttak!!) {
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
