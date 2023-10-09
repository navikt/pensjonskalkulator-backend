package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UtbetalingsperiodeTest {

    @Test
    fun `erLivsvarig is true when sluttalder not defined, false otherwise`() {
        assertTrue(utbetalingsperiode1(null).erLivsvarig)
        assertTrue(utbetalingsperiode2(null).erLivsvarig)
        assertFalse(utbetalingsperiode1(angittSluttalder).erLivsvarig)
        assertFalse(utbetalingsperiode2(angittSluttalder).erLivsvarig)
    }

    @Test
    fun `when startAlder after sluttAlder an exception is thrown`() {
        testStartAfterSluttAlder(start = Alder(3, 0), slutt = Alder(2, 11))
        testStartAfterSluttAlder(start = Alder(4, 10), slutt = Alder(4, 9))
    }

    private companion object {
        private val startalder = Alder(67, 0)
        private val angittSluttalder = Alder(99, 11)

        private fun utbetalingsperiode(start: Alder, slutt: Alder?) =
            Utbetalingsperiode(
                startAlder = start,
                sluttAlder = slutt,
                aarligUtbetalingForventet = 123,
                aarligUtbetalingNedreGrense = 1,
                aarligUtbetalingOvreGrense = 999,
                grad = Uttaksgrad.NULL
            )

        private fun utbetalingsperiode1(slutt: Alder?) = utbetalingsperiode(startalder, slutt)

        private fun utbetalingsperiode2(slutt: Alder?) =
            Utbetalingsperiode(
                startAlder = startalder,
                sluttAlder = slutt,
                aarligUtbetaling = 0,
                grad = Uttaksgrad.HUNDRE_PROSENT
            )

        private fun testStartAfterSluttAlder(start: Alder, slutt: Alder) {
            val exception = assertThrows<IllegalArgumentException> { utbetalingsperiode(start, slutt) }
            assertEquals("startAlder <= sluttAlder", exception.message)
        }
    }
}
