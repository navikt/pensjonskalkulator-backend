package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AldersgrenseMapperV2Test {

    @Test
    fun `dto maps Aldersgrenser to AldersgrenseResultV2`() {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1963,
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAlder = Alder(aar = 62, maaneder = 0),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        val result = AldersgrenseMapperV2.dto(aldersgrenser)

        // Verify mapping according to current implementation
        assertEquals(67, result.normertPensjoneringsalder.aar)
        assertEquals(0, result.normertPensjoneringsalder.maaneder)
        assertEquals(62, result.nedreAldersgrense.aar)
        assertEquals(0, result.nedreAldersgrense.maaneder)
        assertEquals(75, result.oevreAldersgrense.aar)
        assertEquals(0, result.oevreAldersgrense.maaneder)
    }

    @Test
    fun `dto maps Aldersgrenser with non-zero months to AldersgrenseResultV2`() {
        val aldersgrenser = Aldersgrenser(
            aarskull = 1970,
            normalder = Alder(aar = 67, maaneder = 3),
            nedreAlder = Alder(aar = 62, maaneder = 6),
            oevreAlder = Alder(aar = 75, maaneder = 0),
            verdiStatus = VerdiStatus.FAST
        )

        val result = AldersgrenseMapperV2.dto(aldersgrenser)

        // Verify mapping according to current implementation
        assertEquals(67, result.normertPensjoneringsalder.aar)
        assertEquals(3, result.normertPensjoneringsalder.maaneder)
        assertEquals(62, result.nedreAldersgrense.aar)
        assertEquals(6, result.nedreAldersgrense.maaneder)
        assertEquals(75, result.oevreAldersgrense.aar)
        assertEquals(0, result.oevreAldersgrense.maaneder)
    }
}
