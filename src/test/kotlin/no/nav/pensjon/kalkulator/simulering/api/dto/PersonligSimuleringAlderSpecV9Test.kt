package no.nav.pensjon.kalkulator.simulering.api.dto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class PersonligSimuleringAlderSpecV9Test : ShouldSpec({

    should("require non-zero 'år' value") {
        shouldThrow<IllegalArgumentException> {
            PersonligSimuleringAlderSpecV9(aar = -1, maaneder = 11)
        }.message shouldBe "0 <= aar <= 200"
    }

    should("require 'år' of 200 or less") {
        shouldThrow<IllegalArgumentException> {
            PersonligSimuleringAlderSpecV9(aar = 201, maaneder = 0)
        }.message shouldBe "0 <= aar <= 200"
    }

    should("require non-zero 'måneder' value") {
        shouldThrow<IllegalArgumentException> {
            PersonligSimuleringAlderSpecV9(aar = 100, maaneder = -1)
        }.message shouldBe "0 <= maaneder <= 11"
    }

    should("require 'måneder' of 11 or less") {
        shouldThrow<IllegalArgumentException> {
            PersonligSimuleringAlderSpecV9(aar = 0, maaneder = 12)
        }.message shouldBe "0 <= maaneder <= 11"
    }
})
