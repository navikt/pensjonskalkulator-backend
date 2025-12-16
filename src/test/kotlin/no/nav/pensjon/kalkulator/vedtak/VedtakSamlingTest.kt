package no.nav.pensjon.kalkulator.vedtak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

class VedtakSamlingTest : ShouldSpec({

    should("be false if no content") {
        VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            pre2025OffentligAfp = null
        ).hasContent() shouldBe false
    }

    should("be true if containing løpende alderspensjon") {
        VedtakSamling(
            loependeAlderspensjon = LoependeAlderspensjon(
                grad = 20,
                fom = LocalDate.of(2024, 1, 1),
                uttaksgradFom = LocalDate.of(2025, 1, 1),
                utbetalingSisteMaaned = null,
                sivilstand = Sivilstand.SAMBOER
            ),
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            pre2025OffentligAfp = null
        ).hasContent() shouldBe true
    }

    should("be true if containing pre-2025 offentlig AFP") {
        VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            pre2025OffentligAfp = LoependeEntitet(
                fom = LocalDate.of(2024, 1, 1)
            )
        ).hasContent() shouldBe true
    }
})
