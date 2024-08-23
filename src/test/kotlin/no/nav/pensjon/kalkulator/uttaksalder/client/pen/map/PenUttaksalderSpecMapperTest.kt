package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenUtenlandsperiodeSpec
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PenUttaksalderSpecMapperTest {

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
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 67, maaneder = 0),
                inntekt = Inntekt(
                    aarligBeloep = 5_000,
                    tomAlder = Alder(aar = 70, maaneder = 11)
                )
            ),
            utenlandsperiodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidet = true
                )
            )
        )
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1
        )

        val dto = PenUttaksalderSpecMapper.toDto(impersonalSpec, personalSpec)

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
            heltUttak = null, // not specified
            utenlandsperiodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidet = true
                )
            )
        )

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1234
        )

        val dto = PenUttaksalderSpecMapper.toDto(impersonalSpec, personalSpec)

        dto shouldBe PenUttaksalderSpec(
            simuleringType = "ALDER_M_AFP_PRIVAT",
            pid = pid.value,
            sivilstand = "UGIF",
            harEps = false,
            sisteInntekt = 1234,
            gradertUttak = null,
            heltUttak = PenUttaksalderHeltUttakSpec(
                uttakFomAlder = null,
                inntekt = PenUttaksalderInntektSpec(
                    aarligBelop = 0,
                    tomAlder = PenUttaksalderAlderSpec(aar = 75, maaneder = 0)
                )
            ),
            utenlandsperiodeListe = listOf(
                PenUtenlandsperiodeSpec(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = "AUS",
                    arbeidetUtenlands = true
                )
            )
        )
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
            ),
            utenlandsperiodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidet = true
                )
            )
        )
        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            aarligInntektFoerUttak = 1234
        )

        val dto = PenUttaksalderSpecMapper.toDto(impersonalSpec, personalSpec)

        dto shouldBe PenUttaksalderSpec(
            simuleringType = "ALDER_M_AFP_PRIVAT",
            pid = pid.value,
            sivilstand = "UGIF",
            harEps = false,
            sisteInntekt = 1234,
            gradertUttak = PenUttaksalderGradertUttakSpec(grad = "P_80", aarligInntekt = 0),
            heltUttak = PenUttaksalderHeltUttakSpec(
                uttakFomAlder = PenUttaksalderAlderSpec(aar = 65, maaneder = 4),
                inntekt = null
            ),
            utenlandsperiodeListe = listOf(
                PenUtenlandsperiodeSpec(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = "AUS",
                    arbeidetUtenlands = true
                )
            )
        )
    }
}
