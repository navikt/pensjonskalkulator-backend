package no.nav.pensjon.kalkulator.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid

class PossiblyEncryptedPidTest : ShouldSpec({

    context("isEncrypted") {
        context("encrypted PID") {
            should("return 'true'") {
                PossiblyEncryptedPid("contains.dot").isEncrypted shouldBe true
            }
        }

        context("unencrypted PID") {
            should("return 'false'") {
                PossiblyEncryptedPid(pid.value).isEncrypted shouldBe false
            }
        }
    }
})