package no.nav.pensjon.kalkulator.vedtak

import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseServiceTest.Companion.now
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingService
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.MONTH_START
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingServiceTest.Companion.dummyUtbetaling
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
class VedtakMedUtbetalingServiceTest {

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var timeProvider: TimeProvider

    @Mock
    private lateinit var utbetalingService: UtbetalingService

    @Mock
    private lateinit var loependeVedtakService: LoependeVedtakService

    private lateinit var service: VedtakMedUtbetalingService

    @BeforeEach
    fun initialize() {
        Mockito.`when`(pidGetter.pid()).thenReturn(pid)
        Mockito.`when`(timeProvider.time()).thenReturn(now)
        service = VedtakMedUtbetalingService(loependeVedtakService, utbetalingService)
    }

    @Test
    fun hentVedtakMedUtbetaling() = runTest {
        Mockito.`when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = LoependeAlderspensjonDetaljer(
                    grad = 1,
                    fom = AP_START_DATO
                ),
                ufoeretrygd = LoependeVedtakDetaljer(
                    grad = 2,
                    fom = UFOERETRYGD_START_DATO
                ), afpPrivat = LoependeVedtakDetaljer(
                    grad = 3,
                    fom = AFP_PRIVAT_START_DATO
                ), afpOffentlig = LoependeVedtakDetaljer(
                    grad = 4,
                    fom = AFP_OFFENTLIG_START_DATO
                )
            )
        )

        Mockito.`when`(utbetalingService.hentSisteMaanedsUtbetaling()).thenReturn(
            dummyUtbetaling(SISTE_AP_UTBETALING_DATO, posteringsdato = SISTE_AP_UTBETALING_DATO, beloep = BigDecimal.TEN)
        )

        val vedtak = service.hentVedtakMedUtbetaling()

        assertNotNull(vedtak)
        assertEquals(1, vedtak.alderspensjon?.grad)
        assertEquals(AP_START_DATO, vedtak.alderspensjon?.fom)
        assertNotNull(vedtak.alderspensjon?.utbetalingSisteMaaned)
        assertEquals(SISTE_AP_UTBETALING_DATO, vedtak.alderspensjon?.utbetalingSisteMaaned?.posteringsdato)
        assertEquals(BigDecimal.TEN, vedtak.alderspensjon?.utbetalingSisteMaaned?.beloep)
        assertEquals(2, vedtak.ufoeretrygd?.grad)
        assertEquals(UFOERETRYGD_START_DATO, vedtak.ufoeretrygd?.fom)
        assertEquals(3, vedtak.afpPrivat?.grad)
        assertEquals(AFP_PRIVAT_START_DATO, vedtak.afpPrivat?.fom)
        assertEquals(4, vedtak.afpOffentlig?.grad)
        assertEquals(AFP_OFFENTLIG_START_DATO, vedtak.afpOffentlig?.fom)
    }

    val AP_START_DATO = LocalDate.parse("2024-08-01")
    val UFOERETRYGD_START_DATO = LocalDate.parse("2022-01-01")
    val AFP_PRIVAT_START_DATO = LocalDate.parse("2023-01-01")
    val AFP_OFFENTLIG_START_DATO = LocalDate.parse("2024-01-01")
    val SISTE_AP_UTBETALING_DATO = LocalDate.of(2024, Month.JANUARY, MONTH_START)
}