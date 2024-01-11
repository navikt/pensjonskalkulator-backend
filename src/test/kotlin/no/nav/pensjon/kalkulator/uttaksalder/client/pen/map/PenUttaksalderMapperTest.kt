package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.DateFactory
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.Calendar

class PenUttaksalderMapperTest {

    @Test
    fun `toDto maps sivilstand to PEN's value`() {
        val spec = UttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = false,
            sisteInntekt = 1,
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            gradertUttak = GradertUttak(
                grad = Uttaksgrad.SEKSTI_PROSENT,
                uttakFomAlder = Alder(67, 0),
                aarligInntekt = 10_000,
                foedselDato = LocalDate.of(1965, 2, 1)
            )
        )

        val dto = PenUttaksalderMapper.toDto(spec)

        with(dto) {
            assertEquals("12906498357", pid)
            assertEquals("UGIF", sivilstand)
            assertFalse(harEps)
            assertEquals(1, sisteInntekt)
            assertEquals("ALDER_M_AFP_PRIVAT", simuleringType)
            assertEquals("P_60", uttaksgrad)
            assertEquals(10_000, inntektUnderGradertUttak)
            assertEquals(DateFactory.date(2032, Calendar.MARCH), heltUttakDato)
        }
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
