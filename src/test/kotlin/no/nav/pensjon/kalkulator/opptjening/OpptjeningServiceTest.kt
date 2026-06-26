package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient

class OpptjeningServiceTest : ShouldSpec({

    should("håndtere tomme lister") {
        OpptjeningService(
            client = arrangeClient(opptjeningListe = emptyList(), beholdningListe = emptyList()),
            pidGetter = mockk(relaxed = true)
        ).opptjening() shouldBe emptyList()
    }

    should("håndtere tom opptjeningsliste") {
        OpptjeningService(
            client = arrangeClient(opptjeningListe = emptyList(), beholdningListe(aar = 2025)),
            pidGetter = mockk(relaxed = true)
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2025,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 12
            )
        )
    }

    should("håndtere tom beholdningsliste") {
        OpptjeningService(
            client = arrangeClient(opptjeningListe(aar = 2022), beholdningListe = emptyList()),
            pidGetter = mockk(relaxed = true)
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2022,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4,
                pensjonspoengType = "T1",
                beholdning = 0
            )
        )
    }

    should("slå sammen opptjening og beholdning for samme år") {
        OpptjeningService(
            client = arrangeClient(opptjeningListe(aar = 2021), beholdningListe(aar = 2021)),
            pidGetter = mockk(relaxed = true)
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4,
                pensjonspoengType = "T1",
                beholdning = 12
            )
        )
    }

    should("bruke første beholdning hvis flere for samme år") {
        OpptjeningService(
            client = arrangeClient(
                opptjeningListe = emptyList(),
                beholdningListe = listOf(
                    AarligBeholdning(aar = 2021, beholdning = 12),
                    AarligBeholdning(aar = 2021, beholdning = 23)
                )
            ),
            pidGetter = mockk(relaxed = true)
        ).opptjening().singleOrNull()?.beholdning shouldBe 12
    }

    should("inkludere ikke-overlappende opptjening og beholdning, og fylle inn manglende år") {
        OpptjeningService(
            client = arrangeClient(opptjeningListe(aar = 2020), beholdningListe(aar = 2022)),
            pidGetter = mockk(relaxed = true)
        ).opptjening() shouldBe listOf(
            // År med kun opptjening:
            AarligOpptjening(
                aar = 2020,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4,
                pensjonspoengType = "T1",
                beholdning = 0
            ),
            // Manglende år:
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 0
            ),
            // År med kun beholdning:
            AarligOpptjening(
                aar = 2022,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 12
            )
        )
    }
})

private fun arrangeClient(
    opptjeningListe: List<AarligOpptjening>,
    beholdningListe: List<AarligBeholdning>
): PensjonspoengClient =
    mockk {
        every { fetchOpptjeningOgBeholdning(any()) } returns Pair(opptjeningListe, beholdningListe)
    }

private fun opptjeningListe(aar: Int) =
    listOf(
        AarligOpptjening(
            aar,
            pensjonsgivendeInntekt = 1,
            pensjonspoeng = 2.1,
            omsorgspoeng = 3,
            maksimalUfoeregrad = 4,
            pensjonspoengType = "T1",
            beholdning = 0
        )
    )

private fun beholdningListe(aar: Int) =
    listOf(
        AarligBeholdning(
            aar,
            beholdning = 12
        )
    )