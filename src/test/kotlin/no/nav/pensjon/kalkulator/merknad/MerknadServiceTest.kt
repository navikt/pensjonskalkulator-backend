package no.nav.pensjon.kalkulator.merknad

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.merknad.client.MerknadClient

class MerknadServiceTest : ShouldSpec({

    should("utelate år som har tom merknadliste") {
        MerknadService(
            client = arrangeMerknader(
                perAar = mapOf(
                    2021 to emptyList(), // tom merknadliste
                    2022 to listOf(Merknad(kode = MerknadCode.AFP))
                )
            ),
            pidGetter = mockk(relaxed = true)
        ).merknader() shouldBe Merknader(
            perAar = mapOf(2022 to listOf(Merknad(kode = MerknadCode.AFP)))
        )
    }

    should("utelate merknad for 'ingen opptjening'") {
        MerknadService(
            client = arrangeMerknader(
                perAar = mapOf(
                    2021 to listOf(
                        Merknad(kode = MerknadCode.OMSORGSOPPTJENING),
                        Merknad(kode = MerknadCode.INGEN_OPPTJENING)
                    )
                )
            ),
            pidGetter = mockk(relaxed = true)
        ).merknader() shouldBe Merknader(
            perAar = mapOf(2021 to listOf(Merknad(kode = MerknadCode.OMSORGSOPPTJENING)))
        )
    }
})

private fun arrangeMerknader(perAar: Map<Int, List<Merknad>>): MerknadClient =
    mockk { every { fetchMerknader(any()) } returns Merknader(perAar) }