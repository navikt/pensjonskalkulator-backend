package no.nav.pensjon.kalkulator.simulering

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PensjonUtilTest {

    @Test
    fun `pensjoneringsaar is foedselsaar + pensjoneringsalder when foedselsmaaned is not December`() {
        val foedselsdato = LocalDate.of(1963, 11, 30)
        assertEquals(2030, PensjonUtil.pensjoneringsaar(foedselsdato, 67))
    }

    @Test
    fun `pensjoneringsaar is foedselsaar + pensjoneringsalder + 1 when foedselsmaaned is December`() {
        val foedselsdato = LocalDate.of(1963, 12, 1)
        assertEquals(2031, PensjonUtil.pensjoneringsaar(foedselsdato, 67))
    }

    @Test
    fun `foersteUttaksdato is first in month after pensjoneringsalder`() {
        val foedselsdato = LocalDate.of(1963, 2, 10)
        val uttaksdato = PensjonUtil.foersteUttaksdato(foedselsdato, 67)
        assertEquals(LocalDate.of(2030, 3, 1), uttaksdato)
    }
}
