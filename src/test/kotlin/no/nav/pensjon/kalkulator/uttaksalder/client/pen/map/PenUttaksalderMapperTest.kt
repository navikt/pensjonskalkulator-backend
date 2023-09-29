package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PenUttaksalderMapperTest {

    @Test
    fun `toDto maps sivilstand to PEN's value`() {
        val spec = UttaksalderSpec(pid, Sivilstand.UGIFT, false, 1, SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT)
        assertEquals("UGIF", PenUttaksalderMapper.toDto(spec).sivilstand)
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
