package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.testutil.Arrange

class SecurityContextPidExtractorTest : ShouldSpec({

    should("return PID if found in JWT 'pid' claim") {
        Arrange.authentication(claimKey = "pid", claimValue = pid.value)
        SecurityContextPidExtractor().pid() shouldBe pid
    }

    should("return null if no 'pid' claim in JWT") {
        Arrange.authentication(claimKey = "no-pid", claimValue = pid.value)
        SecurityContextPidExtractor().pid() shouldBe null
    }
})
