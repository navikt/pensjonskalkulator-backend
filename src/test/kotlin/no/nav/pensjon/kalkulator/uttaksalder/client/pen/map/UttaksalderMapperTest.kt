package no.nav.pensjon.kalkulator.uttaksalder.client.pen.map

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class UttaksalderMapperTest {

    @Test
    fun `toDto maps sivilstand to PEN's value`() {
        val spec = UttaksalderSpec(pid, Sivilstand.UGIFT, false, 1)
        assertEquals("UGIF", UttaksalderMapper.toDto(spec).sivilstand)
    }
}
