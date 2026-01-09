package no.nav.pensjon.kalkulator.normalder

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.client.NormertPensjonsalderClient
import java.time.LocalDate

class NormertPensjonsalderServiceTest : ShouldSpec({

    context("aldersgrenser") {
        should("return aldersgrenser for årskull") {
            service.aldersgrenser(foedselsdato = LocalDate.of(1965, 1, 1)) shouldBe
                    Aldersgrenser(
                        aarskull = 1965,
                        normalder = Alder(67, 1),
                        nedreAlder = Alder(62, 1),
                        oevreAlder = Alder(75, 1),
                        verdiStatus = VerdiStatus.PROGNOSE
                    )
        }
    }

    context("nedreAlder") {
        should("return nedre aldersgrense for årskull") {
            service.nedreAlder(foedselsdato = LocalDate.of(1975, 6, 15)) shouldBe
                    Alder(63, 11)
        }
    }

    context("normalder") {
        should("return normert pensjonsalder for årskull") {
            service.normalder(foedselsdato = LocalDate.of(1964, 12, 31)) shouldBe
                    Alder(67, 0)
        }
    }
})

private val service =
    NormertPensjonsalderService(normalderClient = arrangeClient())

private fun arrangeClient(): NormertPensjonsalderClient =
    mockk<NormertPensjonsalderClient>().apply {
        every { fetchNormalderListe() } returns
                listOf(
                    Aldersgrenser(
                        aarskull = 1964,
                        normalder = Alder(67, 0),
                        nedreAlder = Alder(62, 0),
                        oevreAlder = Alder(75, 0),
                        verdiStatus = VerdiStatus.PROGNOSE
                    ),
                    Aldersgrenser(
                        aarskull = 1965,
                        normalder = Alder(67, 1),
                        nedreAlder = Alder(62, 1),
                        oevreAlder = Alder(75, 1),
                        verdiStatus = VerdiStatus.PROGNOSE
                    ),
                    Aldersgrenser(
                        aarskull = 1975,
                        normalder = Alder(68, 11),
                        nedreAlder = Alder(63, 11),
                        oevreAlder = Alder(76, 11),
                        verdiStatus = VerdiStatus.PROGNOSE
                    )
                )
    }
