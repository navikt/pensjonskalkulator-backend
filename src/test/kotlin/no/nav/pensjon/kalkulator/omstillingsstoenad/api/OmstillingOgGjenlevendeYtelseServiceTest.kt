package no.nav.pensjon.kalkulator.omstillingsstoenad.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseService
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingsstoenadService
import no.nav.pensjon.kalkulator.sak.SakService

class OmstillingOgGjenlevendeYtelseServiceTest : ShouldSpec({

    should("returnere 'true' når personen mottar omstillingsstønad og gjenlevendeytelse") {
        OmstillingOgGjenlevendeYtelseService(
            omstillingsstoenadService = arrangeOmstillingsstoenad(mottarStoenad = true),
            sakService = arrangeSak(harSakType = true)
        ).harLoependeSaker() shouldBe true
    }

    should("returnere 'false' når personen ikke har noen løpende saker") {
        OmstillingOgGjenlevendeYtelseService(
            omstillingsstoenadService = arrangeOmstillingsstoenad(mottarStoenad = false),
            sakService = arrangeSak(harSakType = false)
        ).harLoependeSaker() shouldBe false
    }

    should("returnere 'true' når personen mottar kun omstillingsstønad") {
        OmstillingOgGjenlevendeYtelseService(
            omstillingsstoenadService = arrangeOmstillingsstoenad(mottarStoenad = true),
            sakService = arrangeSak(harSakType = false)
        ).harLoependeSaker() shouldBe true
    }

    should("returnere 'true' når personen mottar kun gjenlevendeytelse") {
        OmstillingOgGjenlevendeYtelseService(
            omstillingsstoenadService = arrangeOmstillingsstoenad(mottarStoenad = false),
            sakService = arrangeSak(harSakType = true)
        ).harLoependeSaker() shouldBe true
    }
})

private fun arrangeSak(harSakType: Boolean): SakService =
    mockk<SakService>(relaxed = true).apply {
        coEvery { harSakType(any()) } returns harSakType
    }

private fun arrangeOmstillingsstoenad(mottarStoenad: Boolean): OmstillingsstoenadService =
    mockk<OmstillingsstoenadService>().apply {
        coEvery { mottarOmstillingsstoenad() } returns mottarStoenad
    }
