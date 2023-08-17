package no.nav.pensjon.kalkulator.avtale

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class UtbetalingsperiodeTest {

    private val startalder = Alder(67, 1)
    private val angittSluttalder = Alder(99, 12)

    @Test
    fun `erLivsvarig is true when sluttalder not defined, false otherwise`() {
        assertTrue(utbetalingsperiode1(null).erLivsvarig)
        assertTrue(utbetalingsperiode2(null).erLivsvarig)
        assertFalse(utbetalingsperiode1(angittSluttalder).erLivsvarig)
        assertFalse(utbetalingsperiode2(angittSluttalder).erLivsvarig)
    }


    private fun utbetalingsperiode1(slutt: Alder?) =
        Utbetalingsperiode(startalder, slutt, 123, 1, 999, Uttaksgrad.NULL)

    private fun utbetalingsperiode2(slutt: Alder?) =
        Utbetalingsperiode(startalder, slutt, 0, Uttaksgrad.HUNDRE_PROSENT)

}