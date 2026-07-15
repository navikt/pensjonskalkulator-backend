package no.nav.pensjon.kalkulator.sak.client.pen

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.sak.SakStatus

class PenSakStatusTest : ShouldSpec({

    should("map 'løpende' to internal value") {
        PenSakStatus.internalValue(externalValue = "LOPENDE") shouldBe SakStatus.LOEPENDE
    }

    should("map missing value to special internal value 'none'") {
        PenSakStatus.internalValue(externalValue = "") shouldBe SakStatus.NONE
    }

    should("map unknown value to special internal value 'unknown'") {
        PenSakStatus.internalValue(externalValue = "HVA_BEHAGER") shouldBe SakStatus.UNKNOWN
    }
})