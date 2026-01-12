package no.nav.pensjon.kalkulator.ekskludering.api.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak

class EkskluderingAarsakV1Test : ShouldSpec({

    should("mappe intern ekskluderingsårsak til API-ets ekskluderingsårsak") {
        EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.NONE) shouldBe
                EkskluderingAarsakV1.NONE

        EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.HAR_GJENLEVENDEYTELSE) shouldBe
                EkskluderingAarsakV1.HAR_GJENLEVENDEYTELSE

        EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD) shouldBe
                EkskluderingAarsakV1.HAR_LOEPENDE_UFOERETRYGD

        EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.ER_APOTEKER) shouldBe
                EkskluderingAarsakV1.ER_APOTEKER
    }
})
