package no.nav.pensjon.kalkulator.general

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AlderTest {

    @Test
    fun `test legal values`() {
        assertEquals(0, Alder(0, 0).maaneder)
        assertEquals(11, Alder(100, 11).maaneder)
    }

    @Test
    fun `test illegal maaneder values`() {
        testIllegalMaanederValue(-1)
        testIllegalMaanederValue(12)
    }

    private companion object {
        private fun testIllegalMaanederValue(maaneder: Int) {
            assertEquals("0 <= maaneder <= 11", assertThrows<IllegalArgumentException> { Alder(29, maaneder) }.message)
        }
    }
}
