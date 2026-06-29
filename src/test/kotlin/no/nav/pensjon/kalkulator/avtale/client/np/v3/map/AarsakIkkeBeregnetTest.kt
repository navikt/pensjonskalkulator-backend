package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.ManglendeEksternBeregningAarsak

class AarsakIkkeBeregnetTest : ShouldSpec({

    should("map 'generell feil manglende prognose' to internal value") {
        AarsakIkkeBeregnet.internalValue(externalValue = "GENERELL_FEIL_MANGLENDE_PROGNOSE") shouldBe
                ManglendeEksternBeregningAarsak.GENERELL_FEIL_MANGLENDE_PROGNOSE
    }

    should("map missing value to special internal value 'none'") {
        AarsakIkkeBeregnet.internalValue(externalValue = "") shouldBe
                ManglendeEksternBeregningAarsak.NONE
    }

    should("map unknown value to special internal value 'unknown'") {
        AarsakIkkeBeregnet.internalValue(externalValue = "HVA_BEHAGER") shouldBe
                ManglendeEksternBeregningAarsak.UNKNOWN
    }
})