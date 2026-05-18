package no.nav.pensjon.kalkulator.vedtak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstatus
import java.time.LocalDate

class VedtakSamlingTest : ShouldSpec({

    should("be false if no content") {
        VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            tidsbegrensetOffentligAfp = null,
            avdoed = null
        ).hasContent() shouldBe false
    }

    should("be true if containing løpende alderspensjon") {
        VedtakSamling(
            loependeAlderspensjon = LoependeAlderspensjon(
                grad = Uttaksgrad.TJUE_PROSENT,
                fom = LocalDate.of(2024, 1, 1),
                uttaksgradFom = LocalDate.of(2025, 1, 1),
                utbetalingSisteMaaned = null,
                sivilstatus = Sivilstatus.SAMBOER,
                harGjenlevenderett = true,
                harUtenlandsopphold = true
            ),
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            tidsbegrensetOffentligAfp = null,
            avdoed = null
        ).hasContent() shouldBe true
    }

    should("be true if containing tidsbegrenset offentlig AFP") {
        VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = null,
            privatAfp = null,
            tidsbegrensetOffentligAfp = LoependeEntitet(fom = LocalDate.of(2024, 1, 1)),
            avdoed = null
        ).hasContent() shouldBe true
    }
})
