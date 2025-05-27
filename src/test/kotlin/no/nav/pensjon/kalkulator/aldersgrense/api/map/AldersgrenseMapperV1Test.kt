package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AldersgrenseMapperV1Test {

    @Test
    fun `dtoV1 maps Aldersgrenser to AldersgrenseResultV1`() {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1963,
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAlder = Alder(aar = 62, maaneder = 0),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        val result = AldersgrenseMapperV1.dtoV1(aldersgrenser)

        // Verify mapping according to current implementation
        assertEquals(67, result.normertPensjoneringsalder.aar)
        assertEquals(0, result.normertPensjoneringsalder.maaneder)
        assertEquals(62, result.nedreAldersgrense.aar)
        assertEquals(0, result.nedreAldersgrense.maaneder)
    }

    @Test
    fun `dtoV1 maps Aldersgrenser with non-zero months to AldersgrenseResultV1`() {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1970,
            normalder = Alder(aar = 67, maaneder = 3),
            nedreAlder = Alder(aar = 62, maaneder = 6),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        val result = AldersgrenseMapperV1.dtoV1(aldersgrenser)

        // Verify mapping according to current implementation
        assertEquals(67, result.normertPensjoneringsalder.aar)
        assertEquals(3, result.normertPensjoneringsalder.maaneder)
        assertEquals(62, result.nedreAldersgrense.aar)
        assertEquals(6, result.nedreAldersgrense.maaneder)
    }
}
