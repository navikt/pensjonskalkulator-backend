package no.nav.pensjon.kalkulator.tech.security.egress.azuread

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class AzureAdUtilTest : ShouldSpec({

    should("return the default scope for a given service") {
        AzureAdUtil.getDefaultScope("cluster1:namespace1:app1") shouldBe
                "api://cluster1.namespace1.app1/.default"
    }
})
