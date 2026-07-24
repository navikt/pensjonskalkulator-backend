package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.Merknader
import no.nav.pensjon.kalkulator.merknad.client.MerknadClient
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter

class OpptjeningServiceTest : ShouldSpec({

    val pidGetter: PidGetter = mockk(relaxed = true)

    should("håndtere tomme lister") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe = emptyList(), beholdningListe = emptyList()),
            merknadClient = arrangeMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening() shouldBe emptyList()
    }

    should("håndtere tom opptjeningsliste og tom merknadsliste") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe = emptyList(), beholdningListe(aar = 2025)),
            merknadClient = arrangeMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2025,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 12,
                merknadListe = emptyList()
            )
        )
    }

    should("håndtere tom beholdningsliste og tom merknadsliste") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe(aar = 2022), beholdningListe = emptyList()),
            merknadClient = arrangeMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2022,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4,
                pensjonspoengType = "T1",
                beholdning = 0,
                merknadListe = emptyList()
            )
        )
    }

    should("slå sammen opptjening og beholdning for samme år") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe(aar = 2021), beholdningListe(aar = 2021)),
            merknadClient = arrangeMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4,
                pensjonspoengType = "T1",
                beholdning = 12,
                merknadListe = emptyList()
            )
        )
    }

    should("slå sammen opptjening, beholdning og merknader for samme år") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe(aar = 2021), beholdningListe(aar = 2021)),
            merknadClient = arrangeMerknader(aar = 2021, merknad = MerknadCode.AFP),
            pidGetter
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4,
                pensjonspoengType = "T1",
                beholdning = 12,
                merknadListe = listOf(MerknadCode.AFP)
            )
        )
    }

    should("bruke første beholdning hvis flere for samme år") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(
                opptjeningListe = emptyList(),
                beholdningListe = listOf(
                    AarligBeholdning(aar = 2021, beholdning = 12),
                    AarligBeholdning(aar = 2021, beholdning = 23)
                )
            ),
            merknadClient = arrangeMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening().singleOrNull()?.beholdning shouldBe 12
    }

    should("inkludere ikke-overlappende opptjening/beholdning/merknader, og fylle inn manglende år") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe(aar = 2020), beholdningListe(aar = 2022)),
            merknadClient = arrangeMerknader(aar = 2023, merknad = MerknadCode.DAGPENGER),
            pidGetter
        ).opptjening() shouldBe listOf(
            // År med kun opptjening:
            AarligOpptjening(
                aar = 2020,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                omsorgspoeng = 3,
                maksimalUfoeregrad = 4,
                pensjonspoengType = "T1",
                beholdning = 0,
                merknadListe = emptyList()
            ),
            // Manglende år:
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 0,
                merknadListe = emptyList()
            ),
            // År med kun beholdning:
            AarligOpptjening(
                aar = 2022,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 12,
                merknadListe = emptyList()
            ),
            // År med kun merknader:
            AarligOpptjening(
                aar = 2023,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 0,
                merknadListe = listOf(MerknadCode.DAGPENGER)
            )
        )
    }
})

private fun arrangeMerknader(perAar: Map<Int, List<MerknadCode>>): MerknadClient =
    mockk { every { fetchMerknader(any()) } returns Merknader(perAar) }

private fun arrangeMerknader(aar: Int, merknad: MerknadCode): MerknadClient =
    arrangeMerknader(perAar = mapOf(aar to listOf(merknad)))

private fun arrangeOpptjening(
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
            beholdning = 0,
            merknadListe = emptyList()
        )
    )

private fun beholdningListe(aar: Int) =
    listOf(AarligBeholdning(aar, beholdning = 12))