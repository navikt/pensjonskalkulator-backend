package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.testutil.Assertions.assertAlder
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UttaksalderMapperTest {

    @Test
    fun `toDto maps domain object to data transfer object`() {
        val dto = UttaksalderMapper.toDto(Alder(aar = 2024, maaneder = 5))

        with(dto!!) {
            assertEquals(2024, aar)
            assertEquals(5, maaneder)
        }
    }

    @Test
    fun `fromIngressSpecForHeltUttakV1 maps data transfer object to domain object`() {
        val domainObject: ImpersonalUttaksalderSpec = UttaksalderMapper.fromIngressSpecForHeltUttakV1(
            IngressUttaksalderSpecForHeltUttakV1(
                simuleringstype = SimuleringType.ALDERSPENSJON,
                sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
                harEps = true,
                aarligInntektFoerUttakBeloep = 123,
                aarligInntektVsaPensjon = IngressUttaksalderInntektV1(
                    beloep = 456,
                    sluttAlder = IngressUttaksalderAlderV1(aar = 70, maaneder = 2)
                )
            )
        )

        with(domainObject) {
            assertEquals(SimuleringType.ALDERSPENSJON, simuleringType)
            assertEquals(Sivilstand.GJENLEVENDE_PARTNER, sivilstand)
            assertTrue(harEps!!)
            assertEquals(123, aarligInntektFoerUttak)
            assertNull(gradertUttak)
            with(heltUttak!!) {
                assertNull(uttakFomAlder) // this is the value to be found
                with(inntekt!!) {
                    assertAlder(expectedAar = 70, expectedMaaneder = 2, actualAlder = tomAlder)
                    assertEquals(456, aarligBeloep)
                }
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
}
