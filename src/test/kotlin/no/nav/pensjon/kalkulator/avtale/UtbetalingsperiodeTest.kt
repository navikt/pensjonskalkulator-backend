package no.nav.pensjon.kalkulator.avtale

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

class UtbetalingsperiodeTest : ShouldSpec({

    context("erLivsvarig") {
        should("be true when sluttalder not defined, false otherwise") {
            utbetalingsperiode1(null).erLivsvarig shouldBe true
            utbetalingsperiode2(null).erLivsvarig shouldBe true
            utbetalingsperiode1(angittSluttalder).erLivsvarig shouldBe false
            utbetalingsperiode2(angittSluttalder).erLivsvarig shouldBe false
        }
    }

    should("throw an exception when startalder after sluttalder") {
        testStartAfterSluttAlder(start = Alder(3, 0), slutt = Alder(2, 11))
        testStartAfterSluttAlder(start = Alder(4, 10), slutt = Alder(4, 9))
    }
})

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
    shouldThrow<IllegalArgumentException> {
        utbetalingsperiode(start, slutt)
    }.message shouldBe "startAlder <= sluttAlder"
}
