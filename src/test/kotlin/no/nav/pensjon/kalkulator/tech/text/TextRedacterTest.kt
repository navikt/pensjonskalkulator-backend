package no.nav.pensjon.kalkulator.tech.text

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class TextRedacterTest : ShouldSpec({

    should("anonymise person-ID") {
        TextRedacter.redact("""{:"GET /v1/hentafpstatus/07836349269"}""") shouldBe
                """{:"GET /v1/hentafpstatus/(!redacted)"}"""
    }
})
