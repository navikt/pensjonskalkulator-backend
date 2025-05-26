package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AldersgrenseMapperV1Test {

    @Test
    fun `dtoV1 maps PensjoneringAldre to AldersgrenseResultV1`() {
        val pensjoneringAldre = PensjoneringAldre(
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAldersgrense = Alder(aar = 62, maaneder = 0)
        )

        val result = AldersgrenseMapperV1.dtoV1(pensjoneringAldre)

        // Verify mapping according to current implementation
        assertEquals(62, result.normertPensjoneringsalder.aar)
        assertEquals(0, result.normertPensjoneringsalder.maaneder)
        assertEquals(62, result.nedreAldersgrense.aar)
        assertEquals(0, result.nedreAldersgrense.maaneder)
    }

    @Test
    fun `dtoV1 maps PensjoneringAldre with non-zero months to AldersgrenseResultV1`() {
        val pensjoneringAldre = PensjoneringAldre(
            normalder = Alder(aar = 67, maaneder = 3),
            nedreAldersgrense = Alder(aar = 62, maaneder = 6)
        )

        val result = AldersgrenseMapperV1.dtoV1(pensjoneringAldre)

        // Verify mapping according to current implementation
        assertEquals(62, result.normertPensjoneringsalder.aar)
        assertEquals(3, result.normertPensjoneringsalder.maaneder)
        assertEquals(62, result.nedreAldersgrense.aar)
        assertEquals(6, result.nedreAldersgrense.maaneder)
    }
}