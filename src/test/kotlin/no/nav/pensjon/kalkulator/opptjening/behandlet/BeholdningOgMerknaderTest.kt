package no.nav.pensjon.kalkulator.opptjening.behandlet

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class BeholdningOgMerknaderTest : ShouldSpec({

    context("funksjonen 'erRelevant'") {
        context("beholdning er 0 og ingen merknader") {
            should("gi 'false'") {
                BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 0),
                    merknadListe = emptyList()
                ).erRelevant() shouldBe false
            }
        }

        context("beholdning er ikke 0") {
            should("gi 'true'") {
                BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 1),
                    merknadListe = emptyList()
                ).erRelevant() shouldBe true
            }
        }

        context("minst én merknad") {
            should("gi 'true'") {
                BeholdningOgMerknader(
                    beholdning = Beholdning(beloep = 0),
                    merknadListe = listOf(Merknad(kode = MerknadCode.AFP))
                ).erRelevant() shouldBe true
            }
        }
    }
})