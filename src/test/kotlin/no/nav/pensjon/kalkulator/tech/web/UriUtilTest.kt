package no.nav.pensjon.kalkulator.tech.web

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class UriUtilTest : ShouldSpec({

    should("formatAsUri produces a URI given scheme, authority, path") {
        UriUtil.formatAsUri(scheme = "scheme1", authority = "authority1", path = "path1") shouldBe
                "scheme1://authority1/path1"
    }

    should("formatAsUri produces a URI given scheme, authority, but no path") {
        UriUtil.formatAsUri(scheme = "scheme1", authority = "authority1", path = "") shouldBe
                "scheme1://authority1"
    }
})
