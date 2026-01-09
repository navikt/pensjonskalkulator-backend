package no.nav.pensjon.kalkulator.aldersgrense

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.AldersgrenseSpec
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import java.time.LocalDate

class AldersgrenseServiceTest : ShouldSpec({

    should("call normalderService with fødselsdato = 1 January of aarskull year and return aldersgrenser") {
        val spec = AldersgrenseSpec(aarskull = 1963)
        val foedselsdato = LocalDate.of(1963, 1, 1)
        val expectedAldersgrenser = aldersgrenser(1963)

        AldersgrenseService(
            normalderService = arrangeNormalderService(foedselsdato, expectedAldersgrenser)
        ).hentAldersgrenser(spec) shouldBe expectedAldersgrenser
    }

    should("handle different aarskull") {
        val spec = AldersgrenseSpec(aarskull = 1970)
        val foedselsdato = LocalDate.of(1970, 1, 1)
        val expectedAldersgrenser = aldersgrenser(1970)

        AldersgrenseService(
            normalderService = arrangeNormalderService(foedselsdato, expectedAldersgrenser)
        ).hentAldersgrenser(spec) shouldBe expectedAldersgrenser
    }
})

private fun arrangeNormalderService(
    foedselsdato: LocalDate,
    expectedAldersgrenser: Aldersgrenser
): NormertPensjonsalderService =
    mockk<NormertPensjonsalderService>().apply {
        every { aldersgrenser(foedselsdato) } returns expectedAldersgrenser
    }

private fun aldersgrenser(aarskull: Int) =
    Aldersgrenser(
        aarskull,
        normalder = Alder(aar = 67, maaneder = 0),
        nedreAlder = Alder(aar = 62, maaneder = 0),
        oevreAlder = Alder(aar = 75, maaneder = 0),
        verdiStatus = VerdiStatus.FAST
    )
