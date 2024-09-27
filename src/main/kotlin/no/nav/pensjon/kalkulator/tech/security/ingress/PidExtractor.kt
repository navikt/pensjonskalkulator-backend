package no.nav.pensjon.kalkulator.tech.security.ingress

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.enriched
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class PidExtractor : PidGetter {

    private val log = KotlinLogging.logger {}

    override fun pid(): Pid =
        authentication()?.targetPid() ?: missingPid()

    private fun missingPid(): Pid {
        "No PID found".let {
            log.warn { it }
            throw RuntimeException(it)
        }
    }

    private companion object {

        private fun authentication(): EnrichedAuthentication? =
            SecurityContextHolder.getContext().authentication?.enriched()
    }
}
