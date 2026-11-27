package no.nav.pensjon.kalkulator.vedtak.api.v2.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.common.api.acl.CommonV1Sivilstatus
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.vedtak.FremtidigAlderspensjon
import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjon
import no.nav.pensjon.kalkulator.vedtak.Utbetaling
import no.nav.pensjon.kalkulator.vedtak.VedtakSamling
import java.math.BigDecimal
import java.time.LocalDate

class VedtakResultMapperTest : ShouldSpec({

    should("map alderspensjon med standardverdier og separate AFP-verdier") {
        val fom = LocalDate.of(2020, 10, 1)
        val utbetalingsdato = LocalDate.of(2026, 11, 20)
        val fremtidigFom = LocalDate.of(2027, 1, 1)
        val source = VedtakSamling(
            loependeAlderspensjon = LoependeAlderspensjon(
                grad = Uttaksgrad.HUNDRE_PROSENT,
                fom = fom,
                utbetalingSisteMaaned = Utbetaling(beloep = null, posteringsdato = utbetalingsdato),
                sivilstatus = Sivilstatus.UGIFT,
                harGjenlevenderett = false,
                harUtenlandsopphold = true,
                livsvarigOffentligAfp = 1234
            ),
            fremtidigAlderspensjon = FremtidigAlderspensjon(
                grad = Uttaksgrad.FOERTI_PROSENT,
                fom = fremtidigFom,
                sivilstatus = Sivilstatus.UGIFT,
                livsvarigOffentligAfp = 2345
            ),
            privatAfp = null,
            ufoeretrygd = null,
            avdoed = null
        )

        VedtakResultMapper.toDto(source) shouldBe VedtakV2Samling(
            harVedtak = true,
            loependeAlderspensjon = VedtakV2LoependeAlderspensjon(
                grad = 100,
                fom = fom,
                uttaksgradFom = fom,
                sisteUtbetaling = VedtakV2Utbetaling(
                    beloep = BigDecimal.ZERO,
                    utbetalingsdato = utbetalingsdato
                ),
                sivilstatus = CommonV1Sivilstatus.UGIFT,
                harGjenlevenderett = false,
                harUtenlandsopphold = true,
                livsvarigOffentligAfpMaanedsbeloep = 1234
            ),
            fremtidigAlderspensjon = VedtakV2Alderspensjonsuttak(
                grad = 40,
                fom = fremtidigFom,
                livsvarigOffentligAfpMaanedsbeloep = 2345
            ),
            ufoeretrygdgrad = null,
            privatAfpFom = null
        )
    }

    should("map empty vedtaksamling to data transfer object") {
        val source = VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            privatAfp = null,
            ufoeretrygd = null,
            avdoed = null
        )

        VedtakResultMapper.toDto(source) shouldBe VedtakV2Samling(
            harVedtak = false,
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygdgrad = null,
            privatAfpFom = null
        )
    }
})
