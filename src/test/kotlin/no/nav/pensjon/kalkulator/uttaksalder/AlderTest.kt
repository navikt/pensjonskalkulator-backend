package no.nav.pensjon.kalkulator.uttaksalder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class AlderTest {

    @Test
    fun `constructor validates aar`() {
        val exception = assertThrows(IllegalArgumentException::class.java) { Alder(201, 1) }
        assertEquals("0 <= aar <= 200", exception.message)
        assertThrows(IllegalArgumentException::class.java) { Alder(-1, 1) }
    }

    @Test
    fun `constructor validates maaneder`() {
        val exception = assertThrows(IllegalArgumentException::class.java) { Alder(62, 12) }
        assertEquals("0 <= maaneder <= 11", exception.message)
        assertThrows(IllegalArgumentException::class.java) { Alder(62, -1) }
    }
}
