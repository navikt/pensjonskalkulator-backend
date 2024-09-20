package no.nav.pensjon.kalkulator.person

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

/**
 * Alle fødselsnumre og D-numre brukt her er syntetiske/fiktive.
 */
class PidTest {

    @Test
    fun `getValue returns 'invalid' for invalid value`() {
        assertEquals("invalid", Pid("bad value").value)
    }

    @Test
    fun `getValue returns PID value for valid value`() {
        assertEquals("04925398980", Pid("04925398980").value)
    }

    @Test
    fun `getDisplayValue returns 'invalid' for invalid value`() {
        assertEquals("invalid", Pid("bad value").displayValue)
    }

    @Test
    fun `getDisplayValue returns redacted value for valid value`() {
        assertEquals("049253*****", Pid("04925398980").displayValue)
    }

    @Test
    fun `toString returns 'invalid' for invalid value`() {
        assertEquals("invalid", Pid("bad value").toString())
    }

    @Test
    fun `toString returns redacted value for valid value`() {
        assertEquals("049253*****", Pid("04925398980").toString())
    }

    @Test
    fun `equals is true when string values are equal`() {
        assertTrue(Pid("04925398980") == Pid("04925398980"))
    }

    @Test
    fun `equals is false when string values are not equal`() {
        assertFalse(Pid("04925398980") == Pid("12906498357"))
    }

    @Test
    fun `equals is false when values are not both PID`() {
        assertFalse(Pid("04925398980").equals("04925398980"))
    }

    @Test
    fun `equals is false when value is null`() {
        assertFalse(Pid("04925398980").equals(null))
    }

    /**
     * Fødselsnummer fra Test-Norge har +80 i månedsverdi.
     */
    @Test
    fun `dato gir datodel som LocalDate for foedselsnummer fra Test-Norge`() {
        assertEquals(LocalDate.of(1953, 12, 4), Pid("04925398980").dato())
    }

    /**
     * D-nummer har +40 i dagsverdi.
     */
    @Test
    fun `dato gir datodel som LocalDate for D-nummer`() {
        assertEquals(LocalDate.of(1985, 1, 1), Pid("41018512345").dato())
    }

    /**
     * Dolly-nummer har +40 i månedsverdi.
     */
    @Test
    fun `dato gir datodel som LocalDate for Dolly-nummer`() {
        assertEquals(LocalDate.of(1966, 1, 1), Pid("01416637578").dato())
    }

    @Test
    fun `dato gir 1901-01-01 hvis ugyldig PID`() {
        assertEquals(LocalDate.of(1901, 1, 1), Pid("0492539898").dato())
    }

    @Test
    fun `dato gir 1902-02-02 hvis ugyldig datodel`() {
        assertEquals(LocalDate.of(1902, 2, 2), Pid("99416637578").dato())
    }
}
