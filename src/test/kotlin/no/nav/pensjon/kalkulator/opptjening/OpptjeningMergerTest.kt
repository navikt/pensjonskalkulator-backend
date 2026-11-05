package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.opptjening.behandlet.Beholdning
import no.nav.pensjon.kalkulator.opptjening.behandlet.BeholdningOgMerknader
import no.nav.pensjon.kalkulator.opptjening.behandlet.Merknad
import no.nav.pensjon.kalkulator.opptjening.behandlet.MerknadCode
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjening

class OpptjeningMergerTest : ShouldSpec({

    should("håndtere tomme inndata") {
        OpptjeningMerger.merge(
            opptjeningListe = emptyList(),
            beholdningOgMerknaderPerAar = emptyMap()
        ) shouldBe emptyList()
    }

    should("håndtere tomt 'beholdning og merknader'-map") {
        OpptjeningMerger.merge(
            opptjeningListe = listOf(
                SimulertOpptjening(
                    aarstall = 2020,
                    pensjonsgivendeInntektBeloep = 1,
                    pensjonspoeng = 2.3,
                    pensjonsbeholdningBeloep = 4,
                    merknadListe = emptyList()
                )
            ),
            beholdningOgMerknaderPerAar = emptyMap()
        ) shouldBe listOf(
            SimulertOpptjening(
                aarstall = 2020,
                pensjonsgivendeInntektBeloep = 1,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 4,
                merknadListe = emptyList()
            )
        )
    }

    should("håndtere tom opptjeningsliste og tom merknadsliste") {
        OpptjeningMerger.merge(
            opptjeningListe = emptyList(),
            beholdningOgMerknaderPerAar = mapOf(
                2021 to BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 4),
                    merknadListe = emptyList()
                )
            )
        ) shouldBe listOf(
            SimulertOpptjening(
                aarstall = 2021,
                pensjonsgivendeInntektBeloep = 0,
                pensjonspoeng = 0.0,
                pensjonsbeholdningBeloep = 4,
                merknadListe = emptyList()
            )
        )
    }

    should("håndtere 0 beholdning og tom merknadsliste") {
        OpptjeningMerger.merge(
            opptjeningListe = listOf(
                SimulertOpptjening(
                    aarstall = 2021,
                    pensjonsgivendeInntektBeloep = 1,
                    pensjonspoeng = 2.3,
                    pensjonsbeholdningBeloep = 4,
                    merknadListe = emptyList()
                )
            ),
            beholdningOgMerknaderPerAar = mapOf(
                2021 to BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 0),
                    merknadListe = emptyList()
                )
            )
        ) shouldBe listOf(
            SimulertOpptjening(
                aarstall = 2021,
                pensjonsgivendeInntektBeloep = 1,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 4,
                merknadListe = emptyList()
            )
        )
    }

    should("slå sammen opptjening og beholdning for samme år") {
        OpptjeningMerger.merge(
            opptjeningListe = listOf(
                SimulertOpptjening(
                    aarstall = 2021,
                    pensjonsgivendeInntektBeloep = 1,
                    pensjonspoeng = 2.3,
                    pensjonsbeholdningBeloep = 0,
                    merknadListe = emptyList()
                )
            ),
            beholdningOgMerknaderPerAar = mapOf(
                2021 to BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 4),
                    merknadListe = emptyList()
                )
            )
        ) shouldBe listOf(
            SimulertOpptjening(
                aarstall = 2021,
                pensjonsgivendeInntektBeloep = 1,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 4,
                merknadListe = emptyList()
            )
        )
    }

    should("slå sammen opptjening, beholdning og merknader for samme år") {
        OpptjeningMerger.merge(
            opptjeningListe = listOf(
                SimulertOpptjening(
                    aarstall = 2021,
                    pensjonsgivendeInntektBeloep = 1,
                    pensjonspoeng = 2.3,
                    pensjonsbeholdningBeloep = 0,
                    merknadListe = emptyList()
                )
            ),
            beholdningOgMerknaderPerAar = mapOf(
                2021 to BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 4),
                    merknadListe = listOf(Merknad(kode = MerknadCode.AFP))
                )
            )
        ) shouldBe listOf(
            SimulertOpptjening(
                aarstall = 2021,
                pensjonsgivendeInntektBeloep = 1,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 4,
                merknadListe = listOf(Merknad(kode = MerknadCode.AFP))
            )
        )
    }

    should("inkludere ikke-overlappende opptjening/beholdning/merknader, og fylle inn manglende år") {
        OpptjeningMerger.merge(
            opptjeningListe = listOf(
                SimulertOpptjening(
                    aarstall = 2020,
                    pensjonsgivendeInntektBeloep = 1,
                    pensjonspoeng = 2.3,
                    pensjonsbeholdningBeloep = 0,
                    merknadListe = emptyList()
                )
            ),
            beholdningOgMerknaderPerAar = mapOf(
                2022 to beholdningOgMerknader(beholdningBeloep = 4), // kun beholdning
                2023 to beholdningOgMerknader(beholdningBeloep = 0, merknadkode = MerknadCode.DAGPENGER)
            )
        ) shouldBe listOf(
            // År med kun opptjening:
            SimulertOpptjening(
                aarstall = 2020,
                pensjonsgivendeInntektBeloep = 1,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 0,
                merknadListe = emptyList()
            ),
            // Manglende år:
            SimulertOpptjening(
                aarstall = 2021,
                pensjonsgivendeInntektBeloep = 0,
                pensjonspoeng = 0.0,
                pensjonsbeholdningBeloep = 0,
                merknadListe = emptyList()
            ),
            // År med kun beholdning:
            SimulertOpptjening(
                aarstall = 2022,
                pensjonsgivendeInntektBeloep = 0,
                pensjonspoeng = 0.0,
                pensjonsbeholdningBeloep = 4,
                merknadListe = emptyList()
            ),
            // År med kun merknader:
            SimulertOpptjening(
                aarstall = 2023,
                pensjonsgivendeInntektBeloep = 0,
                pensjonspoeng = 0.0,
                pensjonsbeholdningBeloep = 0,
                merknadListe = listOf(Merknad(kode = MerknadCode.DAGPENGER))
            )
        )
    }

    should("håndtere usorterte lister") {
        OpptjeningMerger.merge(
            opptjeningListe = listOf(
                kunOpptjening(aar = 2022, inntekt = 2),
                kunOpptjening(aar = 2023, inntekt = 3),
                kunOpptjening(aar = 2021, inntekt = 1)
            ),
            beholdningOgMerknaderPerAar = mapOf(
                2021 to beholdningOgMerknader(beholdningBeloep = 11, merknadkode = MerknadCode.DAGPENGER),
                2023 to beholdningOgMerknader(beholdningBeloep = 13, merknadkode = MerknadCode.AFP),
                2022 to beholdningOgMerknader(beholdningBeloep = 12, merknadkode = MerknadCode.HELT_UTTAK)
            )
        ) shouldBe listOf(
            SimulertOpptjening(
                aarstall = 2021,
                pensjonsgivendeInntektBeloep = 1,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 11,
                merknadListe = listOf(Merknad(kode = MerknadCode.DAGPENGER))
            ),
            SimulertOpptjening(
                aarstall = 2022,
                pensjonsgivendeInntektBeloep = 2,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 12,
                merknadListe = listOf(Merknad(kode = MerknadCode.HELT_UTTAK))
            ),
            SimulertOpptjening(
                aarstall = 2023,
                pensjonsgivendeInntektBeloep = 3,
                pensjonspoeng = 2.3,
                pensjonsbeholdningBeloep = 13,
                merknadListe = listOf(Merknad(kode = MerknadCode.AFP))
            ),
        )
    }
})

private fun beholdningOgMerknader(beholdningBeloep: Int, merknadkode: MerknadCode? = null) =
    BeholdningOgMerknader(
        beholdning = Beholdning(beloep = beholdningBeloep),
        merknadListe = merknadkode?.let { listOf(Merknad(kode = it)) }.orEmpty()
    )

private fun kunOpptjening(aar: Int, inntekt: Int = 1) =
    SimulertOpptjening(
        aarstall = aar,
        pensjonsgivendeInntektBeloep = inntekt,
        pensjonspoeng = 2.3,
        pensjonsbeholdningBeloep = 0,
        merknadListe = emptyList()
    )