package no.nav.pensjon.kalkulator.tech.security.egress.token.validation

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

/**
 * Checks expiration, allowing a certain leeway period.
 */
@Component
class LeewayExpirationChecker(
    val timeProvider: TimeProvider,
    @Value("\${token.expiration.leeway}") leewaySeconds: String
) : ExpirationChecker {

    private val leeway: Long = leewaySeconds.toLong()

    override fun isExpired(issuedTime: LocalDateTime, expiresInSeconds: Long): Boolean {
        val deadline = issuedTime.plusSeconds(expiresInSeconds + leeway)
        return timeProvider.time().isAfter(deadline)
    }

    override fun time(): LocalDateTime {
        return timeProvider.time()
    }
}
