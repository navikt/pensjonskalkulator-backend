package no.nav.pensjon.kalkulator.ekskludering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.sak.SakType

class EkskluderingAarsakTest : ShouldSpec({

    should("mappe sakstype til ekskluderingsårsak") {
        EkskluderingAarsak.from(SakType.NONE) shouldBe EkskluderingAarsak.NONE
        EkskluderingAarsak.from(SakType.GJENLEVENDEYTELSE) shouldBe EkskluderingAarsak.HAR_GJENLEVENDEYTELSE
        EkskluderingAarsak.from(SakType.UFOERETRYGD) shouldBe EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD
        EkskluderingAarsak.from(SakType.GENERELL) shouldBe EkskluderingAarsak.NONE
    }
})
