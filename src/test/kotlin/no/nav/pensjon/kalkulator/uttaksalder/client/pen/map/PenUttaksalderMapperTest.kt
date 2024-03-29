package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PenUttaksalderMapperTest {

    @Test
    fun `'toDto' maps domain object to PEN-specific data transfer object`() {
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1,
            gradertUttak = UttaksalderGradertUttak(
                grad = Uttaksgrad.SEKSTI_PROSENT,
                aarligInntekt = 10_000,
                foedselDato = LocalDate.of(1965, 2, 1)
            ),
            heltUttak = HeltUttak(Alder(67, 0), Inntekt(5_000, Alder(70, 11)))
        )
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1
        )

        val dto = PenUttaksalderMapper.toDto(impersonalSpec, personalSpec)

        with(dto) {
            assertEquals("12906498357", pid)
            assertEquals("UGIF", sivilstand)
            assertFalse(harEps)
            assertEquals(1, sisteInntekt)
            assertEquals("ALDER_M_AFP_PRIVAT", simuleringType)

            with(dto.gradertUttak!!) {
                assertEquals("P_60", grad)
                assertEquals(10_000, aarligInntekt)
            }

            with(dto.heltUttak) {
                with(uttakFomAlder!!) {
                    assertEquals(67, aar)
                    assertEquals(0, maaneder)
                }

                with(inntekt!!) {
                    assertEquals(5_000, aarligBelop)

                    with(tomAlder) {
                        assertEquals(70, aar)
                        assertEquals(11, maaneder)
                    }
                }
            }
        }
    }

    @Test
    fun `'toDto' uses default 'helt uttak' when not specified`() {
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1,
            gradertUttak = null,
            heltUttak = null // not specified
        )
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1234
        )
        val expected = UttaksalderEgressSpecDto(
            simuleringType = "ALDER_M_AFP_PRIVAT",
            pid = pid.value,
            sivilstand = "UGIF",
            harEps = false,
            sisteInntekt = 1234,
            gradertUttak = null,
            heltUttak = UttaksalderHeltUttakSpecDto(
                uttakFomAlder = null,
                inntekt = UttaksalderInntektDto(
                    aarligBelop = 0,
                    tomAlder = UttaksalderAlderDto(aar = 75, maaneder = 0)
                )
            )
        )

        val dto = PenUttaksalderMapper.toDto(impersonalSpec, personalSpec)

        dto shouldBe expected
    }

    @Test
    fun `'toDto' handles undefined inntekt`() {
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1,
            gradertUttak = UttaksalderGradertUttak(
                grad = Uttaksgrad.AATTI_PROSENT,
                aarligInntekt = 0,
                foedselDato = LocalDate.MIN
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 65, maaneder = 4),
                inntekt = null // undefined
            )
        )
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1234
        )
        val expected = UttaksalderEgressSpecDto(
            simuleringType = "ALDER_M_AFP_PRIVAT",
            pid = pid.value,
            sivilstand = "UGIF",
            harEps = false,
            sisteInntekt = 1234,
            gradertUttak = UttaksalderGradertUttakSpecDto(grad = "P_80", aarligInntekt = 0),
            heltUttak = UttaksalderHeltUttakSpecDto(
                uttakFomAlder = UttaksalderAlderDto(aar = 65, maaneder = 4),
                inntekt = null
            )
        )

        val dto = PenUttaksalderMapper.toDto(impersonalSpec, personalSpec)

        dto shouldBe expected
    }

    @Test
    fun `fromDto obtains maaneder by subtracting 1 from maaned`() {
        val dto = UttaksalderDto(62, 11)

        val uttaksalder = PenUttaksalderMapper.fromDto(dto)

        assertEquals(62, uttaksalder.aar)
        assertEquals(10, uttaksalder.maaneder)
    }

    @Test
    fun `fromDto returns previous aar if maaned is zero`() {
        val dto = UttaksalderDto(64, 0)

        val uttaksalder = PenUttaksalderMapper.fromDto(dto)

        assertEquals(63, uttaksalder.aar)
        assertEquals(11, uttaksalder.maaneder)
    }
}
