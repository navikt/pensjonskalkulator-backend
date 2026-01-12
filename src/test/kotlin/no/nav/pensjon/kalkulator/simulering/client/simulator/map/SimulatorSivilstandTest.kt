package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstand

class SimulatorSivilstandTest : ShouldSpec({

    should("map from sivilstand to PEN's equivalent sivilstand") {
        SimulatorSivilstand.fromInternalValue(Sivilstand.GIFT) shouldBe
                SimulatorSivilstand.GIFT

        SimulatorSivilstand.fromInternalValue(Sivilstand.UGIFT) shouldBe
                SimulatorSivilstand.UGIFT

        SimulatorSivilstand.fromInternalValue(Sivilstand.REGISTRERT_PARTNER) shouldBe
                SimulatorSivilstand.REGISTRERT_PARTNER

        SimulatorSivilstand.fromInternalValue(Sivilstand.ENKE_ELLER_ENKEMANN) shouldBe
                SimulatorSivilstand.ENKE_ELLER_ENKEMANN

        SimulatorSivilstand.fromInternalValue(Sivilstand.UOPPGITT) shouldBe
                SimulatorSivilstand.UDEFINERT
    }
})
