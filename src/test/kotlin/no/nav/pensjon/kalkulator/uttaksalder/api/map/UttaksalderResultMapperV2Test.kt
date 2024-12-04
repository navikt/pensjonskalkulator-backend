package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderResultV2
import org.junit.jupiter.api.Test

class UttaksalderResultMapperV2Test {

    @Test
    fun `resultV2 maps domain object to data transfer object`() {
        UttaksalderResultMapperV2.resultV2(Alder(aar = 2024, maaneder = 5)) shouldBe
                UttaksalderResultV2(aar = 2024, maaneder = 5)
    }
}
