package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.merknad.Merknad
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.Merknader
import no.nav.pensjon.kalkulator.merknad.UttaksgradMerknad

class OpptjeningMerknaderTest : ShouldSpec({

    context("funksjonen 'toInternalValue'") {
        should("konvertere eksternt bestemt objektstruktur til intern domenerepresentasjon") {
            OpptjeningMerknader(
                merknaderPerAar = mapOf(
                    2021 to listOf(
                        OpptjeningMerknad(
                            kode = "GRADERT_UTTAK",
                            uttaksgrad = OpptjeningUttaksgrad(grad = 60, kalendermaanedFom = 7)
                        )
                    ),
                    2022 to listOf(
                        OpptjeningMerknad(
                            kode = "GRADERT_UTTAK",
                            uttaksgrad = OpptjeningUttaksgrad(grad = 60)
                        ),
                        OpptjeningMerknad(
                            kode = "HELT_UTTAK",
                            uttaksgrad = OpptjeningUttaksgrad(grad = 100, kalendermaanedFom = 11)
                        )
                    ),
                    2023 to listOf(
                        OpptjeningMerknad(kode = "DAGPENGER"),
                        OpptjeningMerknad(kode = "UFOREGRAD")
                    )
                )
            ).toInternalValue() shouldBe Merknader(
                perAar = mapOf(
                    2021 to listOf(
                        Merknad(
                            kode = MerknadCode.GRADERT_UTTAK,
                            uttaksgrad = UttaksgradMerknad(grad = Uttaksgrad.SEKSTI_PROSENT, kalendermaanedFom = 7)
                        )
                    ),
                    2022 to listOf(
                        Merknad(
                            kode = MerknadCode.GRADERT_UTTAK,
                            uttaksgrad = UttaksgradMerknad(grad = Uttaksgrad.SEKSTI_PROSENT)
                        ),
                        Merknad(
                            kode = MerknadCode.HELT_UTTAK,
                            uttaksgrad = UttaksgradMerknad(grad = Uttaksgrad.HUNDRE_PROSENT, kalendermaanedFom = 11)
                        )
                    ),
                    2023 to listOf(
                        Merknad(kode = MerknadCode.DAGPENGER),
                        Merknad(kode = MerknadCode.UFOEREGRAD)
                    )
                )
            )
        }
    }
})