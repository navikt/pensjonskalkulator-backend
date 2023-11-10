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
        //       0            10           20
        // ------|------------|------------|-------> seconds
        //     issue         now         expire

        val checker = LeewayExpirationChecker(timeProvider, "0")

        assertFalse(
            checker.isExpired(
                issuedTime = pointInTime.minusSeconds(10),
                expiresInSeconds = 20L // 20s after issued, i.e. 10s from now (point in time)
            )
        )
    }

    @Test
    fun `isExpired returns true when expired`() {
        //       0            6        8        10
        // ------|------------|--------|--------|-------> seconds
        //     issue        leeway   expire    now

        val checker = LeewayExpirationChecker(timeProvider, "2")

        assertTrue(
            checker.isExpired(
                issuedTime = pointInTime.minusSeconds(10),
                expiresInSeconds = 8L // 8s after issued, i.e. expired 2s ago
            )
        )
    }

    @Test
    fun `isExpired returns true when not expired but after leeway start`() {
        //       0            8        9        10
        // ------|------------|--------|--------|-------> seconds
        //     issue        leeway    now     expire
        //                    |<------ 2 ------>|

        val checker = LeewayExpirationChecker(timeProvider, "2")

        assertTrue(
            checker.isExpired(
                issuedTime = pointInTime.minusSeconds(9),
                expiresInSeconds = 10L // 10s after issued, i.e. 1s from now
            )
        )
    }

    @Test
    fun `isExpired returns false when not expired and before leeway start`() {
        //       0            8         9         10
        // ------|------------|---------|---------|-------> seconds
        //     issue         now      leeway    expire
        //                              |<-- 1 -->|

        val checker = LeewayExpirationChecker(timeProvider, "1")

        assertFalse(
            checker.isExpired(
                issuedTime = pointInTime.minusSeconds(8),
                expiresInSeconds = 10L // 10s after issued, i.e. 2s from now
            )
        )
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
