package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PensjonUtilTest {

    @Test
    fun `foersteUttaksdato is first in month after foerste uttaksalder`() {
        val foedselsdato = LocalDate.of(1963, 2, 1)
        val uttaksdato = PensjonUtil.foersteUttaksdato(foedselsdato, Alder(67, 0))
        assertEquals(LocalDate.of(2030, 3, 1), uttaksdato)
    }

    @Test
    fun `foersteUttaksdato includes uttaksmaaned in calculation`() {
        val foedselsdato = LocalDate.of(1963, 3, 31)
        val uttaksdato = PensjonUtil.foersteUttaksdato(foedselsdato, Alder(67, 11))
        assertEquals(LocalDate.of(2031, 3, 1), uttaksdato)
    }
}
