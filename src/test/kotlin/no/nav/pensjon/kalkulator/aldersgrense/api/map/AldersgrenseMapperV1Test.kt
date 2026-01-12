package no.nav.pensjon.kalkulator.aldersgrense.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResultV1
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus

class AldersgrenseMapperV1Test : ShouldSpec({

    should("map aldersgrenser to data transfer object") {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1963,
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAlder = Alder(aar = 62, maaneder = 0),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        val result: AldersgrenseResultV1 = AldersgrenseMapperV1.dtoV1(source = aldersgrenser)

        with(result) {
            normertPensjoneringsalder.aar shouldBe 67
            normertPensjoneringsalder.maaneder shouldBe 0
            nedreAldersgrense.aar shouldBe 62
            nedreAldersgrense.maaneder shouldBe 0
        }
    }

    should("handle non-zero months") {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1970,
            normalder = Alder(aar = 67, maaneder = 3),
            nedreAlder = Alder(aar = 62, maaneder = 6),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        val result: AldersgrenseResultV1 = AldersgrenseMapperV1.dtoV1(source = aldersgrenser)

        with(result) {
            normertPensjoneringsalder.aar shouldBe 67
            normertPensjoneringsalder.maaneder shouldBe 3
            nedreAldersgrense.aar shouldBe 62
            nedreAldersgrense.maaneder shouldBe 6
        }
    }
})
