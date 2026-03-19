package no.nav.pensjon.kalkulator.vedtak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.utbetaling.SamletUtbetaling
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingService
import java.math.BigDecimal
import java.time.LocalDate

class VedtakMedUtbetalingServiceTest : ShouldSpec({

    should("hente vedtak med utbetaling") {
        val vedtakSamling = VedtakMedUtbetalingService(
            vedtakService = arrangeVedtak(),
            utbetalingService = arrangeUtbetaling()
        ).hentVedtakMedUtbetaling()

        with(vedtakSamling) {
            with(loependeAlderspensjon!!) {
                grad shouldBe 1
                fom shouldBe LocalDate.of(2024, 8, 1)
                sivilstatus shouldBe Sivilstatus.GIFT
                with(utbetalingSisteMaaned!!) {
                    posteringsdato shouldBe LocalDate.of(2024, 1, 1)
                    beloep shouldBe BigDecimal.TEN
                }
            }
            with(fremtidigAlderspensjon!!) {
                grad shouldBe 1
                fom shouldBe LocalDate.of(2025, 4, 1)
                sivilstatus shouldBe Sivilstatus.SEPARERT
            }
            with(ufoeretrygd!!) {
                grad shouldBe 2
                fom shouldBe LocalDate.of(2022, 1, 1)
            }
            privatAfp?.fom shouldBe LocalDate.of(2023, 1, 1)
        }
    }
})

private fun arrangeVedtak(): LoependeVedtakService =
    mockk<LoependeVedtakService> {
        every {
            hentLoependeVedtak()
        } returns VedtakSamling(
            loependeAlderspensjon = LoependeAlderspensjon(
                grad = 1,
                fom = LocalDate.of(2024, 8, 1),
                sivilstatus = Sivilstatus.GIFT
            ),
            fremtidigAlderspensjon = FremtidigAlderspensjon(
                grad = 1,
                fom = LocalDate.of(2025, 4, 1),
                sivilstatus = Sivilstatus.SEPARERT
            ),
            ufoeretrygd = LoependeUfoeretrygd(grad = 2, fom = LocalDate.of(2022, 1, 1)),
            privatAfp = LoependeEntitet(fom = LocalDate.of(2023, 1, 1))
        )
    }

private fun arrangeUtbetaling(): UtbetalingService =
    mockk<UtbetalingService> {
        coEvery {
            hentSisteMaanedsUtbetaling()
        } returns SamletUtbetaling(
            posteringsdato = LocalDate.of(2024, 1, 1),
            totalBeloep = BigDecimal.TEN
        )
    }

