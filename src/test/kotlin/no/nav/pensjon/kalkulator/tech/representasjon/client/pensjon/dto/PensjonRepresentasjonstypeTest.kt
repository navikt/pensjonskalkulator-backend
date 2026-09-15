package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjonstype

class PensjonRepresentasjonstypeTest : ShouldSpec({

    context("transferable") {
        context("OK verdi") {
            should("convert from internal domain representation to external representation (data transfer object)") {
                PensjonRepresentasjonstype.transferable(Representasjonstype.VERGE_PENSJON_LES) shouldBe
                        "VERGE_PENSJON_LES"
            }
        }

        context("manglende verdi") {
            should("throw 'illegal argument' exception") {
                shouldThrow<IllegalArgumentException> {
                    PensjonRepresentasjonstype.transferable(null)
                }.message shouldBe "Ingen ekstern verdi i representasjon for representasjonstype null"
            }
        }
    }
})