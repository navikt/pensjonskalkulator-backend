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
import java.time.LocalDate

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
        ).opptjening() shouldBe listOf(kunOpptjening(2022))
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

    context("flere beholdninger for samme år") {
        should("bruke beholdning med seneste dato") {
            OpptjeningService(
                opptjeningClient = arrangeOpptjening(
                    opptjeningListe = emptyList(),
                    beholdningListe = listOf(
                        DatertBeholdning(dato = LocalDate.of(2021, 1, 1), beholdning = 11),
                        DatertBeholdning(dato = LocalDate.of(2021, 3, 1), beholdning = 12),
                        DatertBeholdning(dato = LocalDate.of(2021, 2, 1), beholdning = 13)
                    )
                ),
                merknadClient = arrangeMerknader(perAar = emptyMap()),
                pidGetter
            ).opptjening().singleOrNull()?.beholdning shouldBe 12
        }
    }

    should("inkludere ikke-overlappende opptjening/beholdning/merknader, og fylle inn manglende år") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe(aar = 2020), beholdningListe(aar = 2022)),
            merknadClient = arrangeMerknader(aar = 2023, merknad = MerknadCode.DAGPENGER),
            pidGetter
        ).opptjening() shouldBe listOf(
            // År med kun opptjening:
            kunOpptjening(2020),
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

    should("utelate tom merknadliste for år uten opptjening") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(opptjeningListe(aar = 2020), beholdningListe = emptyList()),
            merknadClient = arrangeMerknader(perAar = mapOf(2021 to emptyList())), // tom merknadliste
            pidGetter
        ).opptjening() shouldBe listOf(
            // År 2020 har kun opptjening:
            kunOpptjening(2020)
            // År 2021 har kun en tom merknadliste (ingen opptjening) og utelates derfor
        )
    }

    should("håndtere usorterte lister") {
        OpptjeningService(
            opptjeningClient = arrangeOpptjening(
                opptjeningListe = listOf(
                    kunOpptjening(aar = 2022, pensjonsgivendeInntekt = 2),
                    kunOpptjening(aar = 2023, pensjonsgivendeInntekt = 3),
                    kunOpptjening(aar = 2021, pensjonsgivendeInntekt = 1)
                ),
                beholdningListe = listOf(
                    beholdning(aar = 2023, beloep = 13),
                    beholdning(aar = 2021, beloep = 11),
                    beholdning(aar = 2022, beloep = 12),
                )
            ),
            merknadClient = arrangeMerknader(
                perAar = mapOf(
                    2021 to listOf(MerknadCode.DAGPENGER),
                    2023 to listOf(MerknadCode.AFP),
                    2022 to listOf(MerknadCode.HELT_UTTAK)
                )
            ),
            pidGetter
        ).opptjening() shouldBe listOf(
            opptjeningCombo(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                beholdning = 11,
                merknad = MerknadCode.DAGPENGER
            ),
            opptjeningCombo(
                aar = 2022,
                pensjonsgivendeInntekt = 2,
                beholdning = 12,
                merknad = MerknadCode.HELT_UTTAK
            ),
            opptjeningCombo(
                aar = 2023,
                pensjonsgivendeInntekt = 3,
                beholdning = 13,
                merknad = MerknadCode.AFP
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
    beholdningListe: List<DatertBeholdning>
): PensjonspoengClient =
    mockk {
        every { fetchOpptjeningOgBeholdning(any()) } returns Pair(opptjeningListe, beholdningListe)
    }

private fun opptjeningListe(aar: Int) =
    listOf(kunOpptjening(aar))

private fun kunOpptjening(aar: Int, pensjonsgivendeInntekt: Int = 1) =
    AarligOpptjening(
        aar,
        pensjonsgivendeInntekt,
        pensjonspoeng = 2.1,
        omsorgspoeng = 3,
        maksimalUfoeregrad = 4,
        pensjonspoengType = "T1",
        beholdning = 0,
        merknadListe = emptyList()
    )

private fun beholdning(aar: Int, beloep: Int) =
    DatertBeholdning(
        dato = LocalDate.of(aar, 1, 1),
        beholdning = beloep
    )

private fun beholdningListe(aar: Int) =
    listOf(beholdning(aar, beloep = 12))

private fun opptjeningCombo(
    aar: Int,
    pensjonsgivendeInntekt: Int,
    beholdning: Int,
    merknad: MerknadCode
) =
    AarligOpptjening(
        aar,
        pensjonsgivendeInntekt,
        pensjonspoeng = 2.1,
        omsorgspoeng = 3,
        maksimalUfoeregrad = 4,
        pensjonspoengType = "T1",
        beholdning,
        merknadListe = listOf(merknad)
    )