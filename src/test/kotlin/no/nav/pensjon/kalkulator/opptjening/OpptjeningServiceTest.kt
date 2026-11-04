package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.opptjening.behandlet.*
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter

class OpptjeningServiceTest : ShouldSpec({

    val pidGetter: PidGetter = mockk(relaxed = true)

    should("håndtere tomme lister") {
        OpptjeningService(
            poengClient = arrangePoeng(opptjeningListe = emptyList()),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening() shouldBe emptyList()
    }

    should("håndtere tom opptjeningsliste og tom merknadsliste") {
        OpptjeningService(
            poengClient = arrangePoeng(opptjeningListe = emptyList()),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(
                perAar = mapOf(2025 to beholdningOgMerknader(beholdningBeloep = 111000)) // tom merknadsliste
            ),
            pidGetter
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2025,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = null,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning = 111000,
                merknadListe = emptyList()
            )
        )
    }

    should("håndtere tom beholdningsliste og tom merknadsliste") {
        OpptjeningService(
            poengClient = arrangePoeng(opptjeningListe(aar = 2022)),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening() shouldBe listOf(kunOpptjening(2022))
    }

    should("slå sammen opptjening og beholdning for samme år") {
        OpptjeningService(
            poengClient = arrangePoeng(opptjeningListe(aar = 2021)),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(
                perAar = mapOf(2021 to beholdningOgMerknader(beholdningBeloep = 111000))
            ),
            pidGetter
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                maksimalUfoeregrad = 4,
                pensjonspoengType = PensjonspoengType.OMSORG_FOR_SYKE_FUNKSJONSHEMMEDE_ELDRE,
                beholdning = 111000,
                merknadListe = emptyList()
            )
        )
    }

    should("slå sammen opptjening, beholdning og merknader for samme år") {
        OpptjeningService(
            poengClient = arrangePoeng(opptjeningListe(aar = 2021)),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(
                perAar = mapOf(2021 to beholdningOgMerknader(beholdningBeloep = 111000, merknadkode = MerknadCode.AFP))
            ),
            pidGetter
        ).opptjening() shouldBe listOf(
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                pensjonspoeng = 2.1,
                maksimalUfoeregrad = 4,
                pensjonspoengType = PensjonspoengType.OMSORG_FOR_SYKE_FUNKSJONSHEMMEDE_ELDRE,
                beholdning = 111000,
                merknadListe = listOf(Merknad(kode = MerknadCode.AFP))
            )
        )
    }

    should("inkludere ikke-overlappende opptjening/beholdning/merknader, og fylle inn manglende år") {
        OpptjeningService(
            poengClient = arrangePoeng(opptjeningListe(aar = 2020)),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(
                perAar = mapOf(
                    2022 to beholdningOgMerknader(beholdningBeloep = 111000), // kun beholdning
                    2023 to beholdningOgMerknader(beholdningBeloep = 0, merknadkode = MerknadCode.DAGPENGER)
                )
            ),
            pidGetter
        ).opptjening() shouldBe listOf(
            // År med kun opptjening:
            kunOpptjening(2020),
            // Manglende år:
            AarligOpptjening(
                aar = 2021,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = null,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning = 0,
                merknadListe = emptyList()
            ),
            // År med kun beholdning:
            AarligOpptjening(
                aar = 2022,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = null,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning = 111000,
                merknadListe = emptyList()
            ),
            // År med kun merknader:
            AarligOpptjening(
                aar = 2023,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = null,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning = 0,
                merknadListe = listOf(Merknad(kode = MerknadCode.DAGPENGER))
            )
        )
    }

    should("håndtere tomt 'beholdning og merknader'-map") {
        OpptjeningService(
            poengClient = arrangePoeng(opptjeningListe(aar = 2020)),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(perAar = emptyMap()),
            pidGetter
        ).opptjening() shouldBe listOf(kunOpptjening(2020))
    }

    should("håndtere usorterte lister") {
        OpptjeningService(
            poengClient = arrangePoeng(
                opptjeningListe = listOf(
                    kunOpptjening(aar = 2022, pensjonsgivendeInntekt = 2),
                    kunOpptjening(aar = 2023, pensjonsgivendeInntekt = 3),
                    kunOpptjening(aar = 2021, pensjonsgivendeInntekt = 1)
                )
            ),
            beholdningOgMerknadService = arrangeBeholdningOgMerknader(
                perAar = mapOf(
                    2021 to beholdningOgMerknader(beholdningBeloep = 11, merknadkode = MerknadCode.DAGPENGER),
                    2023 to beholdningOgMerknader(beholdningBeloep = 13, merknadkode = MerknadCode.AFP),
                    2022 to beholdningOgMerknader(beholdningBeloep = 12, merknadkode = MerknadCode.HELT_UTTAK)
                )
            ),
            pidGetter
        ).opptjening() shouldBe listOf(
            opptjeningCombo(
                aar = 2021,
                pensjonsgivendeInntekt = 1,
                beholdning = 11,
                merknadKode = MerknadCode.DAGPENGER
            ),
            opptjeningCombo(
                aar = 2022,
                pensjonsgivendeInntekt = 2,
                beholdning = 12,
                merknadKode = MerknadCode.HELT_UTTAK
            ),
            opptjeningCombo(
                aar = 2023,
                pensjonsgivendeInntekt = 3,
                beholdning = 13,
                merknadKode = MerknadCode.AFP
            )
        )
    }
})

private fun arrangeBeholdningOgMerknader(perAar: Map<Int, BeholdningOgMerknader>): BeholdningOgMerknadService =
    mockk { every { beholdningOgMerknader() } returns AarvisBeholdningOgMerknader(perAar) }

private fun arrangePoeng(opptjeningListe: List<AarligOpptjening>): PensjonspoengClient =
    mockk { every { fetchPensjonspoeng(any()) } returns opptjeningListe }

private fun beholdningOgMerknader(beholdningBeloep: Int, merknadkode: MerknadCode? = null) =
    BeholdningOgMerknader(
        beholdning = Beholdning(beloep = beholdningBeloep),
        merknadListe = merknadkode?.let { listOf(Merknad(kode = it)) }.orEmpty()
    )

private fun opptjeningListe(aar: Int) =
    listOf(kunOpptjening(aar))

private fun kunOpptjening(aar: Int, pensjonsgivendeInntekt: Int = 1) =
    AarligOpptjening(
        aar,
        pensjonsgivendeInntekt,
        pensjonspoeng = 2.1,
        maksimalUfoeregrad = 4,
        pensjonspoengType = PensjonspoengType.OMSORG_FOR_SYKE_FUNKSJONSHEMMEDE_ELDRE,
        beholdning = 0,
        merknadListe = emptyList()
    )

private fun opptjeningCombo(
    aar: Int,
    pensjonsgivendeInntekt: Int,
    beholdning: Int,
    merknad: Merknad
) =
    AarligOpptjening(
        aar,
        pensjonsgivendeInntekt,
        pensjonspoeng = 2.1,
        maksimalUfoeregrad = 4,
        pensjonspoengType = PensjonspoengType.OMSORG_FOR_SYKE_FUNKSJONSHEMMEDE_ELDRE,
        beholdning,
        merknadListe = listOf(merknad)
    )

private fun opptjeningCombo(
    aar: Int,
    pensjonsgivendeInntekt: Int,
    beholdning: Int,
    merknadKode: MerknadCode
) =
    opptjeningCombo(
        aar,
        pensjonsgivendeInntekt,
        beholdning,
        merknad = Merknad(merknadKode)
    )