package no.nav.pensjon.kalkulator.simulering.api.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class IngressSimuleringSpecV7Test {

    @Test
    fun `IngressSimuleringAlderV7 requires non-zero 'aar' value`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            IngressSimuleringAlderV7(aar = -1, maaneder = 11)
        }

        assertEquals("0 <= aar <= 200", exception.message)
    }

    @Test
    fun `IngressSimuleringAlderV7 requires 'aar' of 200 or less`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            IngressSimuleringAlderV7(aar = 201, maaneder = 0)
        }

        assertEquals("0 <= aar <= 200", exception.message)
    }

    @Test
    fun `IngressSimuleringAlderV7 requires non-zero 'maaneder' value`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            IngressSimuleringAlderV7(aar = 100, maaneder = -1)
        }

        assertEquals("0 <= maaneder <= 11", exception.message)
    }

    @Test
    fun `IngressSimuleringAlderV7 requires 'maaneder' of 11 or less`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            IngressSimuleringAlderV7(aar = 0, maaneder = 12)
        }

        assertEquals("0 <= maaneder <= 11", exception.message)
    }
}
