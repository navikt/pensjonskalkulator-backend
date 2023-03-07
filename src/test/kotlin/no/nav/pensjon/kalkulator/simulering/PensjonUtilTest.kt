package no.nav.pensjon.kalkulator.simulering

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PensjonUtilTest {

    @Test
    fun `pensjonsaar is foedselsaar plus pensjonsalder when fodselsmaaned is not December`() {
        val foedselsdato = LocalDate.of(1963, 11, 30)
        assertEquals(2030, PensjonUtil.pensjonsaar(foedselsdato, 67))
    }

    @Test
    fun `pensjonsaar is foedselsaar plus pensjonsalder + 1 when fodselsmaaned is December`() {
        val foedselsdato = LocalDate.of(1963, 12, 1)
        assertEquals(2031, PensjonUtil.pensjonsaar(foedselsdato, 67))
    }
}
