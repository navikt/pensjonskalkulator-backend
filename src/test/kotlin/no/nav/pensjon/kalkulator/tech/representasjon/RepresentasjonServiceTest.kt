package no.nav.pensjon.kalkulator.tech.representasjon

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.PossiblyEncryptedPid
import no.nav.pensjon.kalkulator.tech.representasjon.client.RepresentasjonClient

class RepresentasjonServiceTest : ShouldSpec({

    context("invalid representasjon") {
        should("return 'not valid'") {
            RepresentasjonService(
                client = arrangeRepresentasjon(isValid = false),
                pidEncrypter = mockk()
            ).hasValidRepresentasjonsforhold(PossiblyEncryptedPid(ENCRYPTED_PID)) shouldBe
                    Representasjon(isValid = false, fullmaktsgiver = null)
        }
    }

    context("valid representasjon") {
        context("valid representasjon - encrypted PID") {
            should("return 'valid'") {
                RepresentasjonService(
                    client = arrangeRepresentasjon(isValid = true),
                    pidEncrypter = mockk()
                ).hasValidRepresentasjonsforhold(PossiblyEncryptedPid(ENCRYPTED_PID)) shouldBe
                        Representasjon(isValid = true, fullmaktsgiver = fullmaktsgiver)
            }
        }

        context("unencrypted valid PID") {
            should("return 'valid'") {
                RepresentasjonService(
                    client = arrangeRepresentasjon(isValid = true),
                    pidEncrypter = mockk { every { encrypt(any()) } returns ENCRYPTED_PID }
                ).hasValidRepresentasjonsforhold(PossiblyEncryptedPid(pid.value)) shouldBe
                        Representasjon(isValid = true, fullmaktsgiver = fullmaktsgiver)
            }
        }

        context("unencrypted invalid PID") {
            should("throw 'illegal argument' exception") {
                shouldThrow<IllegalArgumentException> {
                    RepresentasjonService(
                        client = arrangeRepresentasjon(isValid = true),
                        pidEncrypter = mockk()
                    ).hasValidRepresentasjonsforhold(PossiblyEncryptedPid("ugyldig"))
                }.message shouldBe "PID is invalid: ugyldig"
            }
        }
    }
})

private const val ENCRYPTED_PID = "contains.dot"

private val fullmaktsgiver = Personalia(navn = "F", pid)

private fun arrangeRepresentasjon(isValid: Boolean): RepresentasjonClient =
    mockk {
        every {
            hasValidRepresentasjonsforhold(any())
        } returns Representasjon(isValid, fullmaktsgiver = if (isValid) fullmaktsgiver else null)
    }