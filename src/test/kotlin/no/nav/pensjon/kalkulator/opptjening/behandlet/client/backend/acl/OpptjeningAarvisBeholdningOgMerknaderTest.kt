package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.opptjening.behandlet.*

class OpptjeningAarvisBeholdningOgMerknaderTest : ShouldSpec({

    context("funksjonen 'toInternalValue'") {
        should("konvertere eksternt bestemt objektstruktur til intern domenerepresentasjon") {
            OpptjeningAarvisBeholdningOgMerknader(
                perAar = mapOf(
                    2021 to OpptjeningBeholdningOgMerknader(
                        beholdning = OpptjeningBeholdning(beloep = 21000),
                        merknadListe = listOf(
                            OpptjeningMerknad(
                                kode = "GRADERT_UTTAK",
                                uttaksgrad = OpptjeningUttaksgrad(grad = 60, kalendermaanedFom = 7)
                            )
                        )
                    ),
                    2022 to OpptjeningBeholdningOgMerknader(
                        beholdning = OpptjeningBeholdning(beloep = 22000),
                        merknadListe = listOf(
                            OpptjeningMerknad(
                                kode = "GRADERT_UTTAK",
                                uttaksgrad = OpptjeningUttaksgrad(grad = 60)
                            ),
                            OpptjeningMerknad(
                                kode = "HELT_UTTAK",
                                uttaksgrad = OpptjeningUttaksgrad(grad = 100, kalendermaanedFom = 11)
                            )
                        )
                    ),
                    2023 to OpptjeningBeholdningOgMerknader(
                        beholdning = OpptjeningBeholdning(beloep = 23000),
                        merknadListe = listOf(
                            OpptjeningMerknad(kode = "DAGPENGER"),
                            OpptjeningMerknad(kode = "UFOREGRAD")
                        )
                    )
                )
            ).toInternalValue() shouldBe AarvisBeholdningOgMerknader(
                perAar = mapOf(
                    2021 to BeholdningOgMerknader(
                        beholdning = Beholdning(beloep = 21000),
                        merknadListe = listOf(
                            Merknad(
                                kode = MerknadCode.GRADERT_UTTAK,
                                uttaksgrad = UttaksgradMerknad(grad = Uttaksgrad.SEKSTI_PROSENT, kalendermaanedFom = 7)
                            )
                        )
                    ),
                    2022 to BeholdningOgMerknader(
                        beholdning = Beholdning(beloep = 22000),
                        merknadListe = listOf(
                            Merknad(
                                kode = MerknadCode.GRADERT_UTTAK,
                                uttaksgrad = UttaksgradMerknad(grad = Uttaksgrad.SEKSTI_PROSENT)
                            ),
                            Merknad(
                                kode = MerknadCode.HELT_UTTAK,
                                uttaksgrad = UttaksgradMerknad(grad = Uttaksgrad.HUNDRE_PROSENT, kalendermaanedFom = 11)
                            )
                        )
                    ),
                    2023 to BeholdningOgMerknader(
                        beholdning = Beholdning(beloep = 23000),
                        merknadListe = listOf(
                            Merknad(kode = MerknadCode.DAGPENGER),
                            Merknad(kode = MerknadCode.UFOEREGRAD)
                        )
                    )
                )
            )
        }
    }
})