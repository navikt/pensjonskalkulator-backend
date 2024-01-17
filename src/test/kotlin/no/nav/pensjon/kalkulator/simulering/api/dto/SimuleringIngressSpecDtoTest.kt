package no.nav.pensjon.kalkulator.simulering.api.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class SimuleringIngressSpecDtoTest {

    @Test
    fun `SimuleringHeltUttakIngressDtoV2 requires defined 'til-og-med-alder' if non-zero inntekt`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SimuleringHeltUttakIngressDtoV2(
                uttaksalder = SimuleringAlderDto(aar = 70, maaneder = 0),
                aarligInntektVsaPensjon = SimuleringInntektDtoV2(beloep = 1_000, sluttalder = null)
            )
        }

        assertEquals("sluttalder is mandatory for non-zero beloep", exception.message)
    }

    @Test
    fun `SimuleringAlderDto requires non-zero 'aar' value`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SimuleringAlderDto(aar = -1, maaneder = 11)
        }

        assertEquals("0 <= aar <= 200", exception.message)
    }

    @Test
    fun `SimuleringAlderDto requires 'aar' of 200 or less`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SimuleringAlderDto(aar = 201, maaneder = 0)
        }

        assertEquals("0 <= aar <= 200", exception.message)
    }

    @Test
    fun `SimuleringAlderDto requires non-zero 'maaneder' value`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SimuleringAlderDto(aar = 100, maaneder = -1)
        }

        assertEquals("0 <= maaneder <= 11", exception.message)
    }

    @Test
    fun `SimuleringAlderDto requires 'maaneder' of 11 or less`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SimuleringAlderDto(aar = 0, maaneder = 12)
        }

        assertEquals("0 <= maaneder <= 11", exception.message)
    }
}
