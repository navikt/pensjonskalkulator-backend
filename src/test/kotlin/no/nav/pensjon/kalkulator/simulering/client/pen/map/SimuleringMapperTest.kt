package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.mock.DateFactory.date
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class SimuleringMapperTest {

    @Test
    fun `toDto maps sivilstand, simuleringstype to PEN values`() {
        with(SimuleringMapper.toDto(spec())) {
            assertEquals("UGIF", sivilstand)
            assertEquals("ALDER", simuleringstype)
        }
    }

    private companion object {
        private fun spec() =
            SimuleringSpec(
                SimuleringType.ALDERSPENSJON,
                pid,
                1,
                100,
                date,
                Sivilstand.UGIFT,
                true
            )
    }
}
