package no.nav.pensjon.kalkulator.utbetaling

import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseServiceTest.Companion.now
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.utbetaling.client.UtbetalingClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

@ExtendWith(SpringExtension::class)
class UtbetalingServiceTest {

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var timeProvider: TimeProvider

    @Mock
    private lateinit var utbetalingClient: UtbetalingClient

    private lateinit var service: UtbetalingService

    @BeforeEach
    fun initialize() {
        Mockito.`when`(pidGetter.pid()).thenReturn(pid)
        Mockito.`when`(timeProvider.time()).thenReturn(now)
        service = UtbetalingService(pidGetter, utbetalingClient)
    }

    @Test
    fun `hentSisteMaanedsUtbetaling retunerer siste maanedsutbetaling`() = runTest {
        val utbetaling = dummyUtbetaling(
            fom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
            beloep =  BigDecimal.TEN,
        )

        Mockito.`when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenReturn(
            listOf(
                dummyUtbetaling(
                    LocalDate.of(YEAR, Month.AUGUST, MONTH_START),
                ),
                utbetaling,
                dummyUtbetaling(
                    LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
                ),
            )
        )

        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()

        assertNotNull(sisteUtbetaling)
        assertEquals(utbetaling.fom, sisteUtbetaling!!.fom)
        assertEquals(utbetaling.tom, sisteUtbetaling.tom)
        assertEquals(utbetaling.beloep, sisteUtbetaling.beloep)
        assertEquals(utbetaling.posteringsdato, sisteUtbetaling.posteringsdato)
    }

    @Test
    fun `hentSisteMaanedsUtbetaling retunerer utbetalinger som gjelder kun alderspensjon`() = runTest {
        val utbetaling = dummyUtbetaling(
            fom = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
            beloep = BigDecimal.TEN,
        )

        Mockito.`when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenReturn(
            listOf(
                utbetaling,
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

        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()

        assertNotNull(sisteUtbetaling)
        assertEquals(utbetaling.fom, sisteUtbetaling!!.fom)
        assertEquals(utbetaling.tom, sisteUtbetaling.tom)
        assertEquals(utbetaling.beloep, sisteUtbetaling.beloep)
        assertEquals(utbetaling.posteringsdato, sisteUtbetaling.posteringsdato)
    }

    @Test
    fun `hentSisteMaanedsUtbetaling ignorere utbetalinger med ytelsesperiode stoerre enn en maaned`() = runTest {
        val utbetaling = dummyUtbetaling(
            fom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
            beloep =  BigDecimal.TEN,
        )

        Mockito.`when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenReturn(
            listOf(
                dummyUtbetaling(
                    LocalDate.of(YEAR, Month.AUGUST, MONTH_START),
                ),
                utbetaling,
                dummyUtbetaling(
                    LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
                    LocalDate.of(YEAR, Month.NOVEMBER, MONTH_START),
                ),
                dummyUtbetaling(
                    LocalDate.of(YEAR, Month.AUGUST, MONTH_START),
                    LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
                ),
            )
        )

        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()

        assertNotNull(sisteUtbetaling)
        assertEquals(utbetaling.fom, sisteUtbetaling!!.fom)
        assertEquals(utbetaling.tom, sisteUtbetaling.tom)
        assertEquals(utbetaling.beloep, sisteUtbetaling.beloep)
        assertEquals(utbetaling.posteringsdato, sisteUtbetaling.posteringsdato)
    }

    @Test
    fun `hentSisteMaanedsUtbetaling bruker posteringsdato for aa definere hvilken utbetaling er siste`() = runTest {
        val utbetaling = dummyUtbetaling(
            fom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
            beloep =  BigDecimal.TEN,
            posteringsdato = LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
        )

        Mockito.`when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenReturn(
            listOf(
                dummyUtbetaling(
                    fom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
                    tom = LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
                    posteringsdato = LocalDate.of(YEAR, Month.OCTOBER, 30),
                ),
                utbetaling,
                dummyUtbetaling(
                    LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
                    LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
                    posteringsdato = LocalDate.of(YEAR, Month.OCTOBER, 29),
                ),
                dummyUtbetaling(
                    LocalDate.of(YEAR, Month.OCTOBER, MONTH_START),
                    LocalDate.of(YEAR, Month.OCTOBER, MONTH_END),
                ),
            )
        )

        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()

        assertNotNull(sisteUtbetaling)
        assertEquals(utbetaling.fom, sisteUtbetaling!!.fom)
        assertEquals(utbetaling.tom, sisteUtbetaling.tom)
        assertEquals(utbetaling.beloep, sisteUtbetaling.beloep)
        assertEquals(utbetaling.posteringsdato, sisteUtbetaling.posteringsdato)
    }

    @Test
    fun `erMaanedsUtbetaling returns true for all months between first and last days of the same month`() {
        IntRange(YEAR, 2029).forEach { year ->
            IntRange(1, 12).forEach { month ->
                val fom = LocalDate.of(year, month, MONTH_START)
                val tom = LocalDate.of(year, month, MONTH_START).plusMonths(1).minusDays(1)
                assertTrue(UtbetalingService.erMaanedsUtbetaling(dummyUtbetaling(fom, tom)))
            }
        }
    }

    private fun dummyUtbetaling(
        fom: LocalDate,
        tom: LocalDate? = null,
        beloep: BigDecimal = BigDecimal.ONE,
        posteringsdato: LocalDate? = null,
    ) =
        Utbetaling(
            utbetalingsdato = null,
            posteringsdato = posteringsdato ?: fom.plusDays(MONTH_MIDDLE.toLong()),
            beloep = beloep,
            erUtbetalt = false,
            gjelderAlderspensjon = true,
            fom = fom,
            tom = tom ?: fom.plusMonths(1).minusDays(1)
        )

    companion object {
        const val MONTH_START = 1
        const val MONTH_MIDDLE = 15
        const val MONTH_END = 31
        const val YEAR = 2024
    }
}