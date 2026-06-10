package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class OpptjeningServiceTest : ShouldSpec({

    should("gi opptjeningen hentet med opptjeningsklienten") {
        OpptjeningService(
            client = mockk { every { fetchPensjonspoeng(any()) } returns opptjeningListe },
            pidGetter = mockk(relaxed = true)
        ).opptjening() shouldBe opptjeningListe
    }
})

private val opptjeningListe = listOf(
    AarligOpptjening(
        aar = 2021,
        pensjonsgivendeInntekt = 1,
        pensjonspoeng = 2.1,
        omsorgspoeng = 3,
        maksimalUfoeregrad = 4,
        pensjonspoengType = "T1"
    )
)