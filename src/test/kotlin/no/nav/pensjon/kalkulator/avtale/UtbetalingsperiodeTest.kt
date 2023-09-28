package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class UtbetalingsperiodeTest {

    @Test
    fun `erLivsvarig is true when sluttalder not defined, false otherwise`() {
        assertTrue(utbetalingsperiode1(null).erLivsvarig)
        assertTrue(utbetalingsperiode2(null).erLivsvarig)
        assertFalse(utbetalingsperiode1(angittSluttalder).erLivsvarig)
        assertFalse(utbetalingsperiode2(angittSluttalder).erLivsvarig)
    }

    private companion object {
        private val startalder = Alder(67, 0)
        private val angittSluttalder = Alder(99, 11)

        private fun utbetalingsperiode1(sluttAlder: Alder?) =
            Utbetalingsperiode(
                startAlder = startalder,
                sluttAlder = sluttAlder,
                aarligUtbetalingForventet = 123,
                aarligUtbetalingNedreGrense = 1,
                aarligUtbetalingOvreGrense = 999,
                grad = Uttaksgrad.NULL
            )

        private fun utbetalingsperiode2(sluttAlder: Alder?) =
            Utbetalingsperiode(
                startAlder = startalder,
                sluttAlder = sluttAlder,
                aarligUtbetaling = 0,
                grad = Uttaksgrad.HUNDRE_PROSENT
            )
    }
}
