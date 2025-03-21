package no.nav.pensjon.kalkulator.utbetaling

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseServiceTest.Companion.now
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.utbetaling.client.UtbetalingClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
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
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(timeProvider.time()).thenReturn(now)
        service = UtbetalingService(pidGetter, utbetalingClient)
    }

    @Test
    fun `hentSisteMaanedsUtbetaling retunerer utbetalinger som gjelder kun alderspensjon`() = runTest {
        val utbetaling = dummyUtbetaling(
            fom = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
            beloep = BigDecimal.TEN,
        )

        `when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenReturn(
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

        sisteUtbetaling shouldNotBe null
        sisteUtbetaling!!.totalBeloep shouldBe utbetaling.beloep
        sisteUtbetaling.posteringsdato shouldBe utbetaling.posteringsdato
    }

    @Test
    fun `hentSisteMaanedsUtbetaling feiler med EgressException`() = runTest {
        `when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenThrow(EgressException("Failed to fetch utbetalinger"))
        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()
        sisteUtbetaling shouldBe  null
    }

    @Test
    fun `hentSisteMaanedsUtbetaling retunerer samlet utbetaling for alderspensjon`() = runTest {
        val utbetaling = dummyUtbetaling(
            fom = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
            beloep = BigDecimal.ONE,
            posteringsdato = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_MIDDLE)
        )
        val etterUtbetaling = dummyUtbetaling(
            fom = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_START),
            beloep = BigDecimal.ONE,
            posteringsdato = LocalDate.of(YEAR, Month.SEPTEMBER, MONTH_MIDDLE)
        )

        `when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenReturn(
            listOf(
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

        val sisteUtbetaling = service.hentSisteMaanedsUtbetaling()

        sisteUtbetaling shouldNotBe null
        sisteUtbetaling!!.totalBeloep shouldBe BigDecimal.TWO
        sisteUtbetaling.posteringsdato shouldBe utbetaling.posteringsdato
    }

    @Test
    fun `hentSisteMaanedsUtbetaling retunerer ingen utbetaling hvis det ikke ble utbetalt alderspensjon`() = runTest {

        `when`(utbetalingClient.hentSisteMaanedsUtbetaling(pid)).thenReturn(
            listOf(
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

        sisteUtbetaling shouldBe null
    }

    companion object {
        fun dummyUtbetaling(
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

        const val MONTH_START = 1
        const val MONTH_MIDDLE = 15
        const val MONTH_END = 31
        const val YEAR = 2024
    }
}