package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import org.slf4j.event.Level
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

/**
 * Sends info to the auditing system when a user (NAV employee)
 * performs some task on behalf of another person.
 * The info is formatted according to ArcSight CEF (Common Event Format).
 */
@Component
class Auditor(private val navIdExtractor: SecurityContextNavIdExtractor) {

    private val log = KotlinLogging.logger("AUDIT_LOGGER")

    fun audit( onBehalfOfPid: Pid) {
        log.info { cefEntry(navIdExtractor.id(), onBehalfOfPid).format() }
    }

    private fun cefEntry(userId: String, onBehalfOfPid: Pid) =
        CefEntry(
            timestamp = now(),
            level = Level.INFO,
            deviceEventClassId = DEVICE_EVENT_CLASS_ID,
            name = "Datahenting paa vegne av",
            message = "$USER_TYPE beregner alderspensjon for annen person",
            sourceUserId = userId,
            destinationUserId = onBehalfOfPid.value
        )

    private companion object {
        private const val DEVICE_EVENT_CLASS_ID = "audit:read"
        private const val USER_TYPE = "NAV-ansatt"

        private fun now() = ZonedDateTime.now().toInstant().toEpochMilli()
    }
}
