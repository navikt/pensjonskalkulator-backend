package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderResultV3

class UttaksalderResultMapperV3Test : ShouldSpec({

    should("map domain object to data transfer object") {
        UttaksalderResultMapperV3.resultV3(
            source = Alder(aar = 2024, maaneder = 5)
        ) shouldBe UttaksalderResultV3(aar = 2024, maaneder = 5)
    }
})
