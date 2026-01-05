package no.nav.pensjon.kalkulator.tech.crypto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PidEncryptionServiceTest {

    @Autowired
    private lateinit var service: PidEncryptionService

    @Test
    fun `decrypt encrypted value gives original value`() {
        val encrypted = service.encrypt("abc")

        assertTrue(encrypted.startsWith("e1755645."))
        assertEquals("abc", service.decrypt(encrypted))
    }

    @Test
    fun `encrypt gives error message when too long block`() {
        val exception = assertThrows<RuntimeException> { service.encrypt(value = TOO_LONG_BLOCK) }

        assertEquals(
            "Illegal block size for encryption - abc...XYZ - length ${TOO_LONG_BLOCK.length}",
            exception.message
        )
    }

    private companion object {
        // Max. block size is 53 bytes
        private const val TOO_LONG_BLOCK = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    }
}
