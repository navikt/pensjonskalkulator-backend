package no.nav.pensjon.kalkulator.tech.crypto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PidEncryptionServiceTest {

    @Autowired
    private lateinit var service: PidEncryptionService

    @Test
    fun decrypt() {
        val encrypted = service.encrypt("abc")

        assertTrue(encrypted.startsWith("e1755645."))
        assertEquals("abc", service.decrypt(encrypted))
    }
}
