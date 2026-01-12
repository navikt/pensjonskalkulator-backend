package no.nav.pensjon.kalkulator.tech.crypto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PidEncryptionServiceTest : ShouldSpec() {

    @Autowired
    private lateinit var service: PidEncryptionService

    init {
        should("give original value when decrypting encrypted value") {
            val encrypted = service.encrypt("abc")

            encrypted.startsWith("e1755645.") shouldBe true
            service.decrypt(encrypted) shouldBe "abc"
        }

        should("give error message when encrypting too long block") {
            shouldThrow<RuntimeException> { service.encrypt(value = TOO_LONG_BLOCK) }.message shouldBe
                    "Illegal block size for encryption - abc...XYZ - length ${TOO_LONG_BLOCK.length}"
        }
    }

    private companion object {
        // Max. block size is 53 bytes
        private const val TOO_LONG_BLOCK = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    }
}
