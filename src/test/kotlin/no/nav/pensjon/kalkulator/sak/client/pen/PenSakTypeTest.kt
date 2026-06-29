package no.nav.pensjon.kalkulator.sak.client.pen

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.sak.SakType

class PenSakTypeTest : ShouldSpec({

    should("map 'krigspensjon' to internal value") {
        PenSakType.internalValue(externalValue = "KRIGSP") shouldBe SakType.KRIGSPENSJON
    }

    should("map missing value to special internal value 'none'") {
        PenSakType.internalValue(externalValue = "") shouldBe SakType.NONE
    }

    should("map unknown value to special internal value 'unknown'") {
        PenSakType.internalValue(externalValue = "HVA_BEHAGER") shouldBe SakType.UNKNOWN
    }
})