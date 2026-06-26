package no.nav.pensjon.kalkulator.person

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid

class EncryptedPidTest : ShouldSpec({

    context("argument is String with encrypted PID") {
        should("accept value") {
            EncryptedPid("contains.dot").value shouldBe "contains.dot"
        }
    }

    context("argument is PossiblyEncryptedPid with encrypted PID") {
        should("accept value") {
            EncryptedPid(PossiblyEncryptedPid("contains.dot")).value shouldBe "contains.dot"
        }
    }

    context("argument is String with unencrypted PID") {
        should("throw 'illegal argument' exception") {
            shouldThrow<IllegalArgumentException> {
                EncryptedPid(pid.value)
            }.message shouldBe "Expected encrypted PID"
        }
    }

    context("argument is PossiblyEncryptedPid with unencrypted PID") {
        should("throw 'illegal argument' exception") {
            shouldThrow<IllegalArgumentException> {
                EncryptedPid(PossiblyEncryptedPid(pid.value))
            }.message shouldBe "Expected encrypted PID"
        }
    }
})