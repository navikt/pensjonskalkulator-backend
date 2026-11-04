package no.nav.pensjon.kalkulator.opptjening.behandlet

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class BeholdningOgMerknadServiceTest : ShouldSpec({

    should("videreformidle resultatet fra ekstern tjeneste") {
        val result = AarvisBeholdningOgMerknader(
            perAar = mapOf(
                2021 to BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 111000),
                    merknadListe = emptyList() // tom merknadliste
                ),
                2022 to BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 222000),
                    merknadListe = listOf(Merknad(kode = MerknadCode.AFP))
                )
            )
        )

        BeholdningOgMerknadService(
            client = mockk { every { fetchBeholdningOgMerknader(any()) } returns result },
            pidGetter = mockk(relaxed = true)
        ).beholdningOgMerknader() shouldBe result
    }
})