package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.utbetaling.Utbetaling
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.OekonomiUtbetalingDto
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.Periode
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.Ytelse
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class UtbetalingMapperTest : FunSpec({

    test("fromDto mapper ulike ytelser til utbetalinger") {
        val dto = listOf(
            OekonomiUtbetalingDto(
                utbetalingsdato = LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE),
                posteringsdato = LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE),
                utbetalingsstatus = "18", // UTBETALT_AV_BANK
                forfallsdato = LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE),
                utbetalingNettobeloep = BigDecimal(750),
                ytelseListe = listOf(
                    Ytelse(
                        ytelseskomponentersum = BigDecimal(1000),
                        ytelsesperiode = Periode(
                            fom = LocalDate.of(2021, Month.JANUARY, MONTH_START),
                            tom = LocalDate.of(2021, Month.JANUARY, MONTH_END)
                        ),
                        ytelsestype = "Alderspensjon"
                    ),
                    Ytelse(
                        ytelseskomponentersum = BigDecimal(1100),
                        ytelsesperiode = Periode(
                            fom = LocalDate.of(2021, Month.FEBRUARY, MONTH_START),
                            tom = LocalDate.of(2021, Month.FEBRUARY, 28)
                        ),
                        ytelsestype = "Barnepensjon"
                    )
                ),
            ),
            OekonomiUtbetalingDto(
                utbetalingsdato = null,
                posteringsdato = LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE),
                utbetalingsstatus = "9", // MOTTAT_FRA_FORSYSTEM
                forfallsdato = LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE),
                utbetalingNettobeloep = BigDecimal(750),
                ytelseListe = listOf(
                    Ytelse(
                        ytelseskomponentersum = BigDecimal(1200),
                        ytelsesperiode = Periode(
                            fom = LocalDate.of(2021, Month.MARCH, MONTH_START),
                            tom = LocalDate.of(2021, Month.MAY, MONTH_END)
                        ),
                        ytelsestype = "Alderspensjon"
                    ),
                ),
            )
        )

        val result: List<Utbetaling> = UtbetalingMapper.fromDto(dto)

        result shouldHaveSize 3
        with(result[0]) {
            utbetalingsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
            posteringsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
            beloep shouldBe BigDecimal(1000)
            erUtbetalt shouldBe true
            gjelderAlderspensjon shouldBe true
            fom shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_START)
            tom shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_END)
        }
        with(result[1]) {
            utbetalingsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
            posteringsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
            beloep shouldBe BigDecimal(1100)
            erUtbetalt shouldBe true
            gjelderAlderspensjon shouldBe false
            fom shouldBe LocalDate.of(2021, Month.FEBRUARY, MONTH_START)
            tom shouldBe LocalDate.of(2021, Month.FEBRUARY, 28)
        }
        with(result[2]) {
            utbetalingsdato shouldBe null
            posteringsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
            beloep shouldBe BigDecimal(1200)
            erUtbetalt shouldBe false
            gjelderAlderspensjon shouldBe true
            fom shouldBe LocalDate.of(2021, Month.MARCH, MONTH_START)
            tom shouldBe LocalDate.of(2021, Month.MAY, MONTH_END)
        }
    }

    test("toDto oppretter riktig utfylt request") {
        val result = UtbetalingMapper.toDto(pid)

        with(result) {
            ident shouldBe pid.value
            rolle shouldBe "UTBETALT_TIL"
            periodetype shouldBe "YTELSESPERIODE"
            with(periode) {
                val now = LocalDate.now()
                fom shouldBe now.minusMonths(1).withDayOfMonth(1)
                tom shouldBe now.withDayOfMonth(1).minusDays(1)
            }
        }
    }
})

private const val MONTH_START = 1
private const val MONTH_MIDDLE = 15
private const val MONTH_END = 31
