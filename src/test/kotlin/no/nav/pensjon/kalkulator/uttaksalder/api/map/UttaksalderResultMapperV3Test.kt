package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderResultV3
import org.junit.jupiter.api.Test

class UttaksalderResultMapperV3Test {

    @Test
    fun `resultV3 maps domain object to data transfer object`() {
        UttaksalderResultMapperV3.resultV3(Alder(aar = 2024, maaneder = 5)) shouldBe
                UttaksalderResultV3(aar = 2024, maaneder = 5)
    }
}
