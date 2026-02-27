package no.nav.pensjon.kalkulator.utbetaling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.utbetaling.client.UtbetalingClient
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class UtbetalingServiceTest : ShouldSpec({

    should("returnere utbetalinger som gjelder kun alderspensjon") {
        val service = UtbetalingService(
            pidGetter = mockk(relaxed = true),
            utbetalingClient = arrangeUtbetaling(
                utbetalinger = listOf(
                    utbetaling(
                        fom = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
                        beloep = BigDecimal.TEN,
                    ),
                    Utbetaling(
                        fom = LocalDate.of(YEAR, Month.AUGUST, MONTH_START),
                        tom = LocalDate.of(YEAR, Month.AUGUST, MONTH_END),
                        posteringsdato = LocalDate.of(YEAR, Month.AUGUST, MONTH_MIDDLE),
                        beloep = BigDecimal.ONE,
                        erUtbetalt = true,
                        gjelderAlderspensjon = false,
                        utbetalingsdato = null
                    ),
                    Utbetaling(
                        fom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
                        tom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
                        posteringsdato = LocalDate.of(YEAR, Month.OCTOBER, MONTH_MIDDLE),
                        beloep = BigDecimal.ONE,
                        erUtbetalt = true,
                        gjelderAlderspensjon = false,
                        utbetalingsdato = null
                    ),
                )
            )
        )

        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()

        with(sisteUtbetaling!!) {
            totalBeloep shouldBe BigDecimal.TEN
            posteringsdato shouldBe LocalDate.of(YEAR, Month.SEPTEMBER, 16)
        }
    }

    should("returnere ingenting når henting av utbetalinger feiler") {
        val service = UtbetalingService(
            pidGetter = mockk(relaxed = true),
            utbetalingClient = arrangeFailedUtbetaling()
        )

        service.hentSisteMaanedsUtbetaling() shouldBe null
    }

    should("retunere samlet utbetaling for alderspensjon") {
        val utbetaling = utbetaling(
            fom = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
            beloep = BigDecimal.ONE,
            posteringsdato = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_MIDDLE)
        )
        val etterUtbetaling = utbetaling(
            fom = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
            beloep = BigDecimal.ONE,
            posteringsdato = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_MIDDLE)
        )

        val service = UtbetalingService(
            pidGetter = mockk(relaxed = true),
            utbetalingClient = arrangeUtbetaling(
                utbetalinger = listOf(
                    utbetaling,
                    etterUtbetaling,
                    Utbetaling(
                        fom = LocalDate.of(YEAR, Month.AUGUST, MONTH_START),
                        tom = LocalDate.of(YEAR, Month.AUGUST, MONTH_END),
                        posteringsdato = LocalDate.of(YEAR, Month.AUGUST, MONTH_MIDDLE),
                        beloep = BigDecimal.TEN,
                        erUtbetalt = true,
                        gjelderAlderspensjon = false,
                        utbetalingsdato = null
                    ),
                    Utbetaling(
                        fom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
                        tom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
                        posteringsdato = LocalDate.of(YEAR, Month.OCTOBER, MONTH_MIDDLE),
                        beloep = BigDecimal.TEN,
                        erUtbetalt = true,
                        gjelderAlderspensjon = false,
                        utbetalingsdato = null
                    ),
                )
            )
        )

        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()

        with(sisteUtbetaling!!) {
            totalBeloep shouldBe BigDecimal.TWO
            posteringsdato shouldBe utbetaling.posteringsdato
        }
    }

    should("retunere ingenting hvis det ikke ble utbetalt alderspensjon") {
        val service = UtbetalingService(
            pidGetter = mockk(relaxed = true),
            utbetalingClient = arrangeUtbetaling(
                utbetalinger = listOf(
                    Utbetaling(
                        fom = LocalDate.of(YEAR, Month.AUGUST, MONTH_START),
                        tom = LocalDate.of(YEAR, Month.AUGUST, MONTH_END),
                        posteringsdato = LocalDate.of(YEAR, Month.AUGUST, MONTH_MIDDLE),
                        beloep = BigDecimal.ONE,
                        erUtbetalt = true,
                        gjelderAlderspensjon = false,
                        utbetalingsdato = null
                    ),
                    Utbetaling(
                        fom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
                        tom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
                        posteringsdato = LocalDate.of(YEAR, Month.OCTOBER, MONTH_MIDDLE),
                        beloep = BigDecimal.ONE,
                        erUtbetalt = true,
                        gjelderAlderspensjon = false,
                        utbetalingsdato = null
                    ),
                )
            )
        )

        service.hentSisteMaanedsUtbetaling() shouldBe null
    }
})

private const val MONTH_START = 1
private const val MONTH_MIDDLE = 15
private const val MONTH_END = 31
private const val YEAR = 2024

private fun arrangeUtbetaling(utbetalinger: List<Utbetaling>): UtbetalingClient {
    val utbetalingClient = mockk<UtbetalingClient>().apply {
        coEvery { hentSisteMaanedsUtbetaling(any()) } returns utbetalinger
    }
    return utbetalingClient
}

private fun arrangeFailedUtbetaling(): UtbetalingClient =
    mockk<UtbetalingClient>().apply {
        coEvery {
            hentSisteMaanedsUtbetaling(any())
        } throws EgressException("Failed to fetch utbetalinger")
    }

fun utbetaling(
    fom: LocalDate,
    tom: LocalDate = fom.plusMonths(1).minusDays(1),
    beloep: BigDecimal = BigDecimal.ONE,
    posteringsdato: LocalDate = fom.plusDays(15L)
) =
    Utbetaling(
        utbetalingsdato = null,
        posteringsdato,
        beloep,
        erUtbetalt = false,
        gjelderAlderspensjon = true,
        fom,
        tom
    )

