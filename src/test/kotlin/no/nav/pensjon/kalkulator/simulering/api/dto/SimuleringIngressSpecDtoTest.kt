package no.nav.pensjon.kalkulator.simulering.api.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class SimuleringIngressSpecDtoTest {

    @Test
    fun `SimuleringHeltUttakIngressDto requires defined 'til-og-med-alder' if non-zero inntekt`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SimuleringHeltUttakIngressDto(AlderIngressDto(70, 0), 1_000, null)
        }

        assertEquals("inntektTomAlder is mandatory for non-zero aarligInntektVsaPensjon", exception.message)
    }

    @Test
    fun `AlderIngressDto requires non-zero 'aar' value`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AlderIngressDto(-1, 11)
        }

        assertEquals("0 <= aar <= 200", exception.message)
    }

    @Test
    fun `AlderIngressDto requires 'aar' of 200 or less`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AlderIngressDto(201, 0)
        }

        assertEquals("0 <= aar <= 200", exception.message)
    }

    @Test
    fun `AlderIngressDto requires non-zero 'maaneder' value`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AlderIngressDto(100, -1)
        }

        assertEquals("0 <= maaneder <= 11", exception.message)
    }

    @Test
    fun `AlderIngressDto requires 'maaneder' of 11 or less`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AlderIngressDto(0, 12)
        }

        assertEquals("0 <= maaneder <= 11", exception.message)
    }
}
