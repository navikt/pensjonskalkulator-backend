package no.nav.pensjon.kalkulator.vedtak.api.map

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.api.dto.*
import java.math.BigDecimal
import java.time.LocalDate

class LoependeVedtakMapperV4Test : FunSpec({

    test("map to DTO") {
        val vedtakSamling = VedtakSamling(
            loependeAlderspensjon = LoependeAlderspensjon(
                grad = 100,
                fom = LocalDate.of(2020, 10, 1),
                uttaksgradFom = LocalDate.of(2021, 1, 1),
                utbetalingSisteMaaned = Utbetaling(
                    beloep = BigDecimal("100"),
                    posteringsdato = LocalDate.of(2025, 1, 1)
                ),
                sivilstand = Sivilstand.GIFT
            ),
            fremtidigAlderspensjon = FremtidigAlderspensjon(
                grad = 10,
                fom = LocalDate.of(2021, 12, 1),
                sivilstand = Sivilstand.SKILT
            ),
            ufoeretrygd = LoependeUfoeretrygd(
                grad = 50,
                fom = LocalDate.of(2021, 10, 1)
            ),
            privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 10, 1)),
            pre2025OffentligAfp = LoependeEntitet(fom = LocalDate.of(2024, 2, 1))
        )

        val dto: LoependeVedtakV4 = LoependeVedtakMapperV4.toDto(vedtakSamling)

        dto shouldBe LoependeVedtakV4(
            harLoependeVedtak = true,
            alderspensjon = AlderspensjonDetaljerV4(
                grad = 100,
                fom = LocalDate.of(2020, 10, 1),
                uttaksgradFom = LocalDate.of(2021, 1, 1),
                sisteUtbetaling = UtbetalingV4(
                    beloep = BigDecimal("100"),
                    utbetalingsdato = LocalDate.of(2025, 1, 1)
                ),
                sivilstand = SivilstandV4.GIFT
            ),
            fremtidigAlderspensjon = FremtidigAlderspensjonDetaljerV4(
                grad = 10,
                fom = LocalDate.of(2021, 12, 1)
            ),
            ufoeretrygd = UfoeretrygdDetaljerV4(grad = 50),
            afpPrivat = LoependeFraV4(fom = LocalDate.of(2022, 10, 1)),
            afpOffentlig = null,
            pre2025OffentligAfp = LoependeFraV4(fom = LocalDate.of(2024, 2, 1))
        )
    }

    test("map ingen vedtak to DTO") {
        val vedtakSamling = VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            pre2025OffentligAfp = null
        )

        val dto = LoependeVedtakMapperV4.toDto(vedtakSamling)

        dto shouldBe LoependeVedtakV4(
            harLoependeVedtak = false,
            alderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = UfoeretrygdDetaljerV4(grad = 0),
            afpPrivat = null,
            afpOffentlig = null,
            pre2025OffentligAfp = null
        )
    }
})
