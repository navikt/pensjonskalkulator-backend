package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PensjonUtilTest {

    @Test
    fun `uttakDato is first in month after uttaksalder`() {
        val foedselDato = LocalDate.of(1963, 2, 1)
        val uttakDato = PensjonUtil.uttakDato(foedselDato, Alder(67, 0))
        assertEquals(LocalDate.of(2030, 3, 1), uttakDato)
    }

    @Test
    fun `uttakDato includes uttaksmaaned in calculation`() {
        val foedselDato = LocalDate.of(1963, 3, 31)
        val uttakDato = PensjonUtil.uttakDato(foedselDato, Alder(67, 11))
        assertEquals(LocalDate.of(2031, 3, 1), uttakDato)
    }
}
