package no.nav.pensjon.kalkulator.uttaksalder

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder

class AlderTest : ShouldSpec({

    should("validate måneder") {
        shouldThrow<IllegalArgumentException> {
            Alder(aar = 62, maaneder = 12)
        }.message shouldBe "0 <= maaneder <= 11"

        shouldThrow<IllegalArgumentException> { Alder(aar = 62, maaneder = -1) }
    }
})
