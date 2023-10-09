package no.nav.pensjon.kalkulator.general

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AlderTest {

    @Test
    fun `test legal values`() {
        assertEquals(0, Alder(0, 0).maaneder)
        assertEquals(11, Alder(100, 11).maaneder)
    }

    @Test
    fun `test lessThanOrEqualTo`() {
        assertTrue(Alder(99, 11) lessThanOrEqualTo null)
        assertTrue(Alder(1, 5) lessThanOrEqualTo Alder(1, 5))
        assertTrue(Alder(2, 11) lessThanOrEqualTo Alder(3, 0))
        assertFalse(Alder(3, 0) lessThanOrEqualTo Alder(2, 11))
        assertFalse(Alder(4, 10) lessThanOrEqualTo Alder(4, 9))
    }

    @Test
    fun `test illegal maaneder values`() {
        testIllegalMaanederValue(-1)
        testIllegalMaanederValue(12)
    }

    private companion object {
        private fun testIllegalMaanederValue(maaneder: Int) {
            val exception = assertThrows<IllegalArgumentException> { Alder(29, maaneder) }
            assertEquals("0 <= maaneder <= 11", exception.message)
        }
    }
}
