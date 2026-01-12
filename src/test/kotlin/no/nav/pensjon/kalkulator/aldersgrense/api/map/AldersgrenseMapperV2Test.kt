package no.nav.pensjon.kalkulator.aldersgrense.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResultV2
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.PersonAlderV2
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus

class AldersgrenseMapperV2Test : ShouldSpec({

    should("map aldersgrenser to AldersgrenseResultV2") {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1963,
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAlder = Alder(aar = 62, maaneder = 0),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        AldersgrenseMapperV2.dto(source = aldersgrenser) shouldBe
                AldersgrenseResultV2(
                    normertPensjoneringsalder = PersonAlderV2(aar = 67, maaneder = 0),
                    nedreAldersgrense = PersonAlderV2(aar = 62, maaneder = 0),
                    oevreAldersgrense = PersonAlderV2(aar = 75, maaneder = 0)
                )
    }

    should("map aldersgrenser with non-zero months to AldersgrenseResultV2") {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1970,
            normalder = Alder(aar = 67, maaneder = 3),
            nedreAlder = Alder(aar = 62, maaneder = 6),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        AldersgrenseMapperV2.dto(source = aldersgrenser) shouldBe
                AldersgrenseResultV2(
                    normertPensjoneringsalder = PersonAlderV2(aar = 67, maaneder = 3),
                    nedreAldersgrense = PersonAlderV2(aar = 62, maaneder = 6),
                    oevreAldersgrense = PersonAlderV2(aar = 75, maaneder = 0)
                )
    }
})
