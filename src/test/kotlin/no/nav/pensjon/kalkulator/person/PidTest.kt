package no.nav.pensjon.kalkulator.person

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

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
}
