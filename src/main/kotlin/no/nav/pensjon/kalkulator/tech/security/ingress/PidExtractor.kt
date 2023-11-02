package no.nav.pensjon.kalkulator.tech.security.ingress

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.enriched
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class PidExtractor : PidGetter {

    override fun pid(): Pid = SecurityContextHolder.getContext().authentication.enriched().pid ?: missingPid()

    private companion object {
        private fun missingPid(): Pid {
            throw RuntimeException("No PID found")
        }
    }
}
