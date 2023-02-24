package no.nav.pensjon.kalkulator.tech.security.egress.token.validation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class LeewayExpirationCheckerTest {

    @Mock
    private lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun initialize() {
        `when`(timeProvider.time()).thenReturn(pointInTime)
    }

    @Test
    fun `isExpired returns true when expired and no leeway`() {
        val checker = LeewayExpirationChecker(timeProvider, "0")
        assertTrue(checker.isExpired(pointInTime.minusSeconds(10), 1L)) // expired 1s ago
    }

    @Test
    fun `isExpired returns false when not expired`() {
        val checker = LeewayExpirationChecker(timeProvider, "0")
        assertFalse(checker.isExpired(pointInTime.minusSeconds(10), 20L)) // expires in 10s
    }

    @Test
    fun `isExpired returns false when within leeway period`() {
        val checker = LeewayExpirationChecker(timeProvider, "5")
        assertFalse(checker.isExpired(pointInTime.minusSeconds(10), 9L)) // expired 1s ago
    }

    @Test
    fun `isExpired returns true when outside of leeway period`() {
        val checker = LeewayExpirationChecker(timeProvider, "2")
        assertTrue(checker.isExpired(pointInTime.minusSeconds(10), 5L)) // expired 5s ago
    }

    @Test
    fun `time returns time-provider time`() {
        val checker = LeewayExpirationChecker(timeProvider, "1")
        assertEquals(pointInTime, checker.time())
    }

    companion object {
        private val pointInTime: LocalDateTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
    }
}
