package no.nav.pensjon.kalkulator.vedtak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.utbetaling.SamletUtbetaling
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingService
import java.math.BigDecimal
import java.time.LocalDate

class VedtakMedUtbetalingServiceTest : ShouldSpec({

    should("hente vedtak med utbetaling") {
        VedtakMedUtbetalingService(
            vedtakService = arrangeVedtak(),
            utbetalingService = arrangeUtbetaling()
        ).hentVedtakMedUtbetaling() shouldBe
                vedtakSamling(
                    utbetalingSisteMaaned = Utbetaling(
                        beloep = BigDecimal.TEN,
                        posteringsdato = LocalDate.of(2024, 1, 1)
                    )
                )
    }
})

private fun vedtakSamling(utbetalingSisteMaaned: Utbetaling?) =
    VedtakSamling(
        loependeAlderspensjon = LoependeAlderspensjon(
            grad = 1,
            fom = LocalDate.of(2020, 10, 1),
            uttaksgradFom = LocalDate.of(2021, 1, 1),
            utbetalingSisteMaaned = utbetalingSisteMaaned,
            sivilstatus = Sivilstatus.UGIFT,
        ),
        fremtidigAlderspensjon = FremtidigAlderspensjon(
            grad = 3,
            fom = LocalDate.of(2023, 10, 1),
            sivilstatus = Sivilstatus.GIFT
        ),
        ufoeretrygd = LoependeUfoeretrygd(grad = 2, fom = LocalDate.of(2021, 10, 1)),
        privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 10, 1)),
        avdoed = InformasjonOmAvdoed(
            pid = pid,
            doedsdato = LocalDate.of(2025, 6, 14),
            foersteAlderspensjonVirkningsdato = LocalDate.of(2021, 1, 1),
            aarligPensjonsgivendeInntektErMinst1G = true,
            harTilstrekkeligMedlemskapIFolketrygden = false,
            antallAarUtenlands = 3,
            erFlyktning = true
        )
    )

private fun arrangeUtbetaling(): UtbetalingService =
    mockk<UtbetalingService> {
        coEvery {
            hentSisteMaanedsUtbetaling()
        } returns SamletUtbetaling(
            posteringsdato = LocalDate.of(2024, 1, 1),
            totalBeloep = BigDecimal.TEN
        )
    }

private fun arrangeVedtak(): LoependeVedtakService =
    mockk<LoependeVedtakService> {
        every {
            hentLoependeVedtak()
        } returns vedtakSamling(utbetalingSisteMaaned = null)
    }
