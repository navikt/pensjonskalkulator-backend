package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SimulatorUttaksgradTest {
    @Test
    fun `fromInternalValue maps from uttaksgrad to PEN's equivalent uttaksgrad`() {
        assertEquals(SimulatorUttaksgrad.NULL, SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.NULL))

        assertEquals(SimulatorUttaksgrad.TJUE_PROSENT, SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.TJUE_PROSENT))

        assertEquals(
            SimulatorUttaksgrad.FOERTI_PROSENT,
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.FOERTI_PROSENT)
        )

        assertEquals(SimulatorUttaksgrad.FEMTI_PROSENT, SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.FEMTI_PROSENT))

        assertEquals(
            SimulatorUttaksgrad.SEKSTI_PROSENT,
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.SEKSTI_PROSENT)
        )
        assertEquals(SimulatorUttaksgrad.AATTI_PROSENT, SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.AATTI_PROSENT))

        assertEquals(
            SimulatorUttaksgrad.HUNDRE_PROSENT,
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.HUNDRE_PROSENT)
        )
    }
}
