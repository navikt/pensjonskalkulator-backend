package no.nav.pensjon.kalkulator.ekskludering

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.sak.RelevantSakStatus
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.SakType
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService

class EkskluderingFacadeTest : FunSpec({

    test("'ekskluderingPgaSakEllerApoteker' gir normalt 'false'") {
        EkskluderingFacade(
            sakService = arrangeSak(type = SakType.NONE),
            tjenestepensjonService = arrangeApoteker(erApoteker = false)
        ).ekskluderingPgaSakEllerApoteker() shouldBe
                EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)
    }

    test("'ekskluderingPgaSakEllerApoteker' gir 'true' ved gjenlevendeytelse`") {
        EkskluderingFacade(
            sakService = arrangeSak(type = SakType.GJENLEVENDEYTELSE),
            tjenestepensjonService = arrangeApoteker(erApoteker = false)
        ).ekskluderingPgaSakEllerApoteker() shouldBe
                EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.HAR_GJENLEVENDEYTELSE)
    }

    test("'ekskluderingPgaSakEllerApoteker' gir 'true' for apoteker`") {
        EkskluderingFacade(
            sakService = arrangeSak(type = SakType.NONE),
            tjenestepensjonService = arrangeApoteker(erApoteker = true)
        ).ekskluderingPgaSakEllerApoteker() shouldBe
                EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
    }

    test("'apotekerEkskludering' gir normalt 'false'") {
        EkskluderingFacade(
            sakService = arrangeSak(type = SakType.NONE),
            tjenestepensjonService = arrangeApoteker(erApoteker = false)
        ).apotekerEkskludering() shouldBe
                EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)
    }

    test("'apotekerEkskludering' gir 'true' for apoteker`") {
        EkskluderingFacade(
            sakService = arrangeSak(type = SakType.NONE),
            tjenestepensjonService = arrangeApoteker(erApoteker = true)
        ).apotekerEkskludering() shouldBe
                EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
    }
})

private fun arrangeApoteker(erApoteker: Boolean): TjenestepensjonService =
    mockk<TjenestepensjonService>().apply {
        every { erApoteker() } returns erApoteker
    }

private fun arrangeSak(type: SakType): SakService =
    mockk<SakService>().apply {
        every { sakStatus() } returns RelevantSakStatus(harSak = type != SakType.NONE, sakType = type)
    }
