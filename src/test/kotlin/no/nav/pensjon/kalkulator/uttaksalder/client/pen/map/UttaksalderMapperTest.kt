package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class UttaksalderMapperTest {

    @Test
    fun `toDto maps sivilstand to PEN's value`() {
        val spec = UttaksalderSpec(pid, Sivilstand.UGIFT, false, 1)
        assertEquals("UGIF", UttaksalderMapper.toDto(spec).sivilstand)
    }

    @Test
    fun `fromDto subtracts 1 from month`() {
        val dto = UttaksalderDto(62, 12)

        val uttaksalder = UttaksalderMapper.fromDto(dto)

        assertEquals(62, uttaksalder.aar)
        assertEquals(11, uttaksalder.maaneder)
    }

    @Test
    fun `fromDto subtracts 1 from month and handles zero month`() {
        val dto = UttaksalderDto(64, 0)

        val uttaksalder = UttaksalderMapper.fromDto(dto)

        assertEquals(63, uttaksalder.aar)
        assertEquals(12, uttaksalder.maaneder)
    }
}
