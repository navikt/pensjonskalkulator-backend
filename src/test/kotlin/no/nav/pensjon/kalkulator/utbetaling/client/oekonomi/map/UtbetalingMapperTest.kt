package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.utbetaling.Utbetaling
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_END
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_MIDDLE
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_START
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.OekonomiUtbetalingDto
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.Periode
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.Ytelse
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class UtbetalingMapperTest : FunSpec({

    test("fromDto mapper ulike ytelser til utbetalinger") {

        // Arrange
        val dto = listOf(
            OekonomiUtbetalingDto(
                utbetalingsdato = LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE),
                posteringsdato = LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE),
                utbetalingsstatus = "18", //UTBETALT_AV_BANK
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
                utbetalingsstatus = "9", //MOTTAT_FRA_FORSYSTEM
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

        // Act
        val result: List<Utbetaling> = UtbetalingMapper.fromDto(dto)

        // Assert
        result.size shouldBe 3
        result[0].utbetalingsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
        result[0].posteringsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
        result[0].beloep shouldBe BigDecimal(1000)
        result[0].erUtbetalt shouldBe true
        result[0].gjelderAlderspensjon shouldBe true
        result[0].fom shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_START)
        result[0].tom shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_END)

        result[1].utbetalingsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
        result[1].posteringsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
        result[1].beloep shouldBe BigDecimal(1100)
        result[1].erUtbetalt shouldBe true
        result[1].gjelderAlderspensjon shouldBe false
        result[1].fom shouldBe LocalDate.of(2021, Month.FEBRUARY, MONTH_START)
        result[1].tom shouldBe LocalDate.of(2021, Month.FEBRUARY, 28)

        result[2].utbetalingsdato shouldBe null
        result[2].posteringsdato shouldBe LocalDate.of(2021, Month.JANUARY, MONTH_MIDDLE)
        result[2].beloep shouldBe BigDecimal(1200)
        result[2].erUtbetalt shouldBe false
        result[2].gjelderAlderspensjon shouldBe true
        result[2].fom shouldBe LocalDate.of(2021, Month.MARCH, MONTH_START)
        result[2].tom shouldBe LocalDate.of(2021, Month.MAY, MONTH_END)
    }
})
