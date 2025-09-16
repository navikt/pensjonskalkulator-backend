package no.nav.pensjon.kalkulator.ekskludering.api.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.ekskludering.api.dto.*

class EkskluderingMapperTest : FunSpec({

    test("'statusV1' should map to data transfer object version 1") {
        val source = EkskluderingStatus(
            ekskludert = true,
            aarsak = EkskluderingAarsak.HAR_GJENLEVENDEYTELSE
        )

        EkskluderingMapper.statusV1(source) shouldBe
                EkskluderingStatusV1(
                    ekskludert = true,
                    aarsak = EkskluderingAarsakV1.HAR_GJENLEVENDEYTELSE
                )
    }

    test("'statusV2' should map to data transfer object version 2") {
        val source = EkskluderingStatus(
            ekskludert = true,
            aarsak = EkskluderingAarsak.ER_APOTEKER
        )

        EkskluderingMapper.statusV2(source) shouldBe
                EkskluderingStatusV2(
                    ekskludert = true,
                    aarsak = EkskluderingAarsakV2.ER_APOTEKER
                )
    }

    test("'apotekerStatusV1' should map to data transfer object version 1") {
        val source = EkskluderingStatus(
            ekskludert = true,
            aarsak = EkskluderingAarsak.ER_APOTEKER
        )

        EkskluderingMapper.apotekerStatusV1(source) shouldBe
                ApotekerStatusV1(
                    apoteker = true,
                    aarsak = EkskluderingAarsakV2.ER_APOTEKER
                )
    }
})
