package no.nav.pensjon.kalkulator.vedtak.api.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.vedtak.*
import java.math.BigDecimal
import java.time.LocalDate

class VedtakResultMapperTest : ShouldSpec({

    should("map vedtaksamling to data transfer object") {
        val vedtakSamling = VedtakSamling(
            loependeAlderspensjon = LoependeAlderspensjon(
                grad = 100,
                fom = LocalDate.of(2020, 10, 1),
                uttaksgradFom = LocalDate.of(2021, 1, 1),
                utbetalingSisteMaaned = Utbetaling(
                    beloep = BigDecimal("100"),
                    posteringsdato = LocalDate.of(2025, 1, 1)
                ),
                sivilstatus = Sivilstatus.GIFT
            ),
            fremtidigAlderspensjon = FremtidigAlderspensjon(
                grad = 10,
                fom = LocalDate.of(2021, 12, 1),
                sivilstatus = Sivilstatus.SKILT
            ),
            ufoeretrygd = LoependeUfoeretrygd(
                grad = 50,
                fom = LocalDate.of(2021, 10, 1)
            ),
            privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 10, 1)),
            pre2025OffentligAfp = LoependeEntitet(fom = LocalDate.of(2024, 2, 1))
        )

        VedtakResultMapper.toDto(vedtakSamling) shouldBe VedtakV1Samling(
            harVedtak = true,
            loependeAlderspensjon = VedtakV1LoependeAlderspensjon(
                grad = 100,
                fom = LocalDate.of(2020, 10, 1),
                uttaksgradFom = LocalDate.of(2021, 1, 1),
                sisteUtbetaling = VedtakV1Utbetaling(
                    beloep = BigDecimal("100"),
                    utbetalingsdato = LocalDate.of(2025, 1, 1)
                ),
                sivilstatus = VedtakV1Sivilstatus.GIFT
            ),
            fremtidigAlderspensjon = VedtakV1Alderspensjonsuttak(
                grad = 10,
                fom = LocalDate.of(2021, 12, 1)
            ),
            ufoeretrygdgrad = 50,
            privatAfpFom = LocalDate.of(2022, 10, 1),
            tidsbegrensetOffentligAfpFom = LocalDate.of(2024, 2, 1)
        )
    }

    should("map empty vedtaksamling to data transfer object") {
        val vedtakSamling = VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            pre2025OffentligAfp = null
        )

        VedtakResultMapper.toDto(vedtakSamling) shouldBe VedtakV1Samling(
            harVedtak = false,
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygdgrad = null,
            privatAfpFom = null,
            tidsbegrensetOffentligAfpFom = null
        )
    }
})