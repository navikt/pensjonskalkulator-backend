package no.nav.pensjon.kalkulator.tech.security.egress

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityContextEnricher(
    val tokenSuppliers: EgressTokenSuppliersByService,
    private val securityContextPidExtractor: SecurityContextPidExtractor
) {

    fun enrichAuthentication(request: HttpServletRequest) {
        val headerPid = request.getHeader(CustomHttpHeaders.PID)?.let(::Pid)
        val pid = headerPid ?: securityContextPidExtractor.pid()

        with(SecurityContextHolder.getContext()) {
            authentication = authentication?.let { EnrichedAuthentication(it, tokenSuppliers, pid) }
        }
    }
}
