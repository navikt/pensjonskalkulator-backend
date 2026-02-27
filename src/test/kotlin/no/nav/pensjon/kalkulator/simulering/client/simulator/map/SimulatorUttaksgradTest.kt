package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Uttaksgrad

class SimulatorUttaksgradTest : ShouldSpec({

    context("fromInternalValue") {
        should("map from uttaksgrad to PEN's equivalent uttaksgrad") {
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.NULL) shouldBe SimulatorUttaksgrad.NULL
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.TJUE_PROSENT) shouldBe SimulatorUttaksgrad.TJUE_PROSENT
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.FOERTI_PROSENT) shouldBe SimulatorUttaksgrad.FOERTI_PROSENT
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.FEMTI_PROSENT) shouldBe SimulatorUttaksgrad.FEMTI_PROSENT
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.SEKSTI_PROSENT) shouldBe SimulatorUttaksgrad.SEKSTI_PROSENT
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.AATTI_PROSENT) shouldBe SimulatorUttaksgrad.AATTI_PROSENT
            SimulatorUttaksgrad.fromInternalValue(Uttaksgrad.HUNDRE_PROSENT) shouldBe SimulatorUttaksgrad.HUNDRE_PROSENT
        }
    }
})
