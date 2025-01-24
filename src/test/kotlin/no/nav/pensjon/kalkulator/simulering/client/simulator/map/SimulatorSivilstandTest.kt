package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.person.Sivilstand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SimulatorSivilstandTest {

    @Test
    fun `fromInternalValue maps from sivilstand to PEN's equivalent sivilstand`() {
        assertEquals(SimulatorSivilstand.GIFT, SimulatorSivilstand.fromInternalValue(Sivilstand.GIFT))

        assertEquals(SimulatorSivilstand.UGIFT, SimulatorSivilstand.fromInternalValue(Sivilstand.UGIFT))

        assertEquals(
            SimulatorSivilstand.REGISTRERT_PARTNER,
            SimulatorSivilstand.fromInternalValue(Sivilstand.REGISTRERT_PARTNER)
        )

        assertEquals(
            SimulatorSivilstand.ENKE_ELLER_ENKEMANN,
            SimulatorSivilstand.fromInternalValue(Sivilstand.ENKE_ELLER_ENKEMANN)
        )

        assertEquals(SimulatorSivilstand.UDEFINERT, SimulatorSivilstand.fromInternalValue(Sivilstand.UOPPGITT))
    }
}
