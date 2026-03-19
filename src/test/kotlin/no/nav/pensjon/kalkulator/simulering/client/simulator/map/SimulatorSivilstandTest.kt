package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstatus

class SimulatorSivilstandTest : ShouldSpec({

    should("map from sivilstatus to PEN's equivalent") {
        SimulatorSivilstatus.fromInternalValue(Sivilstatus.GIFT) shouldBe
                SimulatorSivilstatus.GIFT

        SimulatorSivilstatus.fromInternalValue(Sivilstatus.UGIFT) shouldBe
                SimulatorSivilstatus.UGIFT

        SimulatorSivilstatus.fromInternalValue(Sivilstatus.REGISTRERT_PARTNER) shouldBe
                SimulatorSivilstatus.REGISTRERT_PARTNER

        SimulatorSivilstatus.fromInternalValue(Sivilstatus.ENKE_ELLER_ENKEMANN) shouldBe
                SimulatorSivilstatus.ENKE_ELLER_ENKEMANN

        SimulatorSivilstatus.fromInternalValue(Sivilstatus.UOPPGITT) shouldBe
                SimulatorSivilstatus.UDEFINERT
    }
})
