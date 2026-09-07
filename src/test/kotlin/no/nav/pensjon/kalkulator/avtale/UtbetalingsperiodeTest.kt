package no.nav.pensjon.kalkulator.avtale

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.NorskPensjonAlderDto
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
        testStartAfterSluttAlder(start = NorskPensjonAlderDto(3, 0), slutt = NorskPensjonAlderDto(2, 11))
        testStartAfterSluttAlder(start = NorskPensjonAlderDto(4, 10), slutt = NorskPensjonAlderDto(4, 9))
    }
})

private val startalder = NorskPensjonAlderDto(67, 1)
private val angittSluttalder = NorskPensjonAlderDto(99, 12)

private fun utbetalingsperiode(start: NorskPensjonAlderDto, slutt: NorskPensjonAlderDto?) =
    Utbetalingsperiode(
        startAlder = start,
        sluttAlder = slutt,
        aarligUtbetalingForventet = 123,
        aarligUtbetalingNedreGrense = 1,
        aarligUtbetalingOvreGrense = 999,
        grad = Uttaksgrad.NULL
    )

private fun utbetalingsperiode1(slutt: NorskPensjonAlderDto?) = utbetalingsperiode(startalder, slutt)

private fun utbetalingsperiode2(slutt: NorskPensjonAlderDto?) =
    Utbetalingsperiode(
        startAlder = startalder,
        sluttAlder = slutt,
        aarligUtbetaling = 0,
        grad = Uttaksgrad.HUNDRE_PROSENT
    )

private fun testStartAfterSluttAlder(start: NorskPensjonAlderDto, slutt: NorskPensjonAlderDto) {
    shouldThrow<IllegalArgumentException> {
        utbetalingsperiode(start, slutt)
    }.message shouldBe "startAlder <= sluttAlder"
}
