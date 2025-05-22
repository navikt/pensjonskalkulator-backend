package no.nav.pensjon.kalkulator.normalder

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.client.NormertPensjonsalderClient
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate

class NormertPensjonsalderServiceTest : FunSpec({

    test("'aldersgrenser' should return aldersgrenser for aarskull") {
        service.aldersgrenser(foedselsdato = LocalDate.of(1965, 1, 1)) shouldBe Aldersgrenser(
            aarskull = 1965,
            normalder = Alder(67, 1),
            nedreAlder = Alder(62, 1),
            oevreAlder = Alder(75, 1),
            verdiStatus = VerdiStatus.PROGNOSE
        )
    }

    test("'nedreAlder' should return nedre aldersgrense for aarskull") {
        service.nedreAlder(foedselsdato = LocalDate.of(1975, 6, 15)) shouldBe Alder(63, 11)
    }

    test("'normalder' should return normert pensjonsalder for aarskull") {
        service.normalder(foedselsdato = LocalDate.of(1964, 12, 31)) shouldBe Alder(67, 0)
    }
})

private val service =
    NormertPensjonsalderService(normalderClient = arrangeClient())

private fun arrangeClient(): NormertPensjonsalderClient =
    mock(NormertPensjonsalderClient::class.java).also {
        `when`(it.fetchNormalderListe()).thenReturn(
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
        )
    }
