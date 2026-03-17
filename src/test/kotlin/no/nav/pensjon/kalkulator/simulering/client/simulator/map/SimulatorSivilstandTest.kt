package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstatus

class SimulatorSivilstandTest : ShouldSpec({

    should("map from sivilstand to PEN's equivalent sivilstand") {
        SimulatorSivilstand.fromInternalValue(Sivilstatus.GIFT) shouldBe
                SimulatorSivilstand.GIFT

        SimulatorSivilstand.fromInternalValue(Sivilstatus.UGIFT) shouldBe
                SimulatorSivilstand.UGIFT

        SimulatorSivilstand.fromInternalValue(Sivilstatus.REGISTRERT_PARTNER) shouldBe
                SimulatorSivilstand.REGISTRERT_PARTNER

        SimulatorSivilstand.fromInternalValue(Sivilstatus.ENKE_ELLER_ENKEMANN) shouldBe
                SimulatorSivilstand.ENKE_ELLER_ENKEMANN

        SimulatorSivilstand.fromInternalValue(Sivilstatus.UOPPGITT) shouldBe
                SimulatorSivilstand.UDEFINERT
    }
})
