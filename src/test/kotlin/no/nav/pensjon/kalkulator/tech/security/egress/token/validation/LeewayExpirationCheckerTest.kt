package no.nav.pensjon.kalkulator.tech.security.egress.token.validation

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

class LeewayExpirationCheckerTest : ShouldSpec({

    context("isExpired") {
        should("return 'true' when expired and no leeway") {
            LeewayExpirationChecker(
                timeProvider = arrangeTime(),
                leewaySeconds = "0"
            ).isExpired(
                issuedTime = pointInTime.minusSeconds(10),
                expiresInSeconds = 1L
            ) shouldBe true
        }

        should("return 'false' when not expired") {
            //       0            10           20
            // ------|------------|------------|-------> seconds
            //     issue         now         expire

            LeewayExpirationChecker(
                timeProvider = arrangeTime(),
                leewaySeconds = "0"
            ).isExpired(
                issuedTime = pointInTime.minusSeconds(10),
                expiresInSeconds = 20L // 20 seconds after issued, i.e., 10 seconds from now (point in time)
            ) shouldBe false
        }

        should("return 'true' when expired") {
            //       0            6        8        10
            // ------|------------|--------|--------|-------> seconds
            //     issue        leeway   expire    now

            LeewayExpirationChecker(
                timeProvider = arrangeTime(),
                leewaySeconds = "2"
            ).isExpired(
                issuedTime = pointInTime.minusSeconds(10),
                expiresInSeconds = 8L // 8 seconds after issued, i.e., expired 2 seconds ago
            ) shouldBe true
        }

        should("return 'true' when not expired but after leeway start") {
            //       0            8        9        10
            // ------|------------|--------|--------|-------> seconds
            //     issue        leeway    now     expire
            //                    |<------ 2 ------>|

            LeewayExpirationChecker(
                timeProvider = arrangeTime(),
                leewaySeconds = "2"
            ).isExpired(
                issuedTime = pointInTime.minusSeconds(9),
                expiresInSeconds = 10L // 10 seconds after issued, i.e., 1 second from now
            ) shouldBe true
        }

        should("return 'false' when not expired and before leeway start") {
            //       0            8         9         10
            // ------|------------|---------|---------|-------> seconds
            //     issue         now      leeway    expire
            //                              |<-- 1 -->|

            LeewayExpirationChecker(
                timeProvider = arrangeTime(),
                leewaySeconds = "1"
            ).isExpired(
                issuedTime = pointInTime.minusSeconds(8),
                expiresInSeconds = 10L // 10 seconds after issued, i.e., 2 seconds from now
            ) shouldBe false
        }
    }

    context("time") {
        should("return the time-provider's time") {
            LeewayExpirationChecker(
                timeProvider = arrangeTime(),
                leewaySeconds = "1"
            ).time() shouldBe pointInTime
        }
    }
})

private val pointInTime =
    LocalDateTime.of(2023, 1, 1, 12, 0, 0)

private fun arrangeTime(): TimeProvider =
    mockk<TimeProvider>().apply {
        every { time() } returns pointInTime
    }

