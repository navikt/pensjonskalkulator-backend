package no.nav.pensjon.kalkulator.tech.security.egress

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.crypto.PidEncryptionService
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength

@Component
class SecurityContextEnricher(
    val tokenSuppliers: EgressTokenSuppliersByService,
    private val securityContextPidExtractor: SecurityContextPidExtractor,
    private val pidEncryptionService: PidEncryptionService
) {

    fun enrichAuthentication(request: HttpServletRequest) {
        val headerPid = pidFromHeader(request)?.let(::Pid)
        val pid = headerPid ?: securityContextPidExtractor.pid()

        with(SecurityContextHolder.getContext()) {
            authentication = authentication?.let { EnrichedAuthentication(
                initialAuth = it,
                egressTokenSuppliersByService = tokenSuppliers,
                pid = pid,
                isOnBehalf = headerPid != null
            ) }
        }
    }

    private fun pidFromHeader(request: HttpServletRequest): String? {
        val header = request.getHeader(CustomHttpHeaders.PID)
        if (!hasLength(header)) return null
        return if (header.contains(".")) pidEncryptionService.decrypt(header) else header
    }
}
