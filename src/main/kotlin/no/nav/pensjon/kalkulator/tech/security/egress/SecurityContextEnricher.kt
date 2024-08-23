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
    private val pidDecrypter: PidEncryptionService
) {
    fun enrichAuthentication(request: HttpServletRequest) {
        val headerPid = pidFromHeader(request)?.let(::Pid)
        val pid = headerPid ?: securityContextPidExtractor.pid()

        with(SecurityContextHolder.getContext()) {
            authentication = authentication?.let {
                EnrichedAuthentication(
                    initialAuth = it,
                    egressTokenSuppliersByService = tokenSuppliers,
                    pid = pid,
                    isOnBehalf = headerPid != null
                )
            } ?: anonymousAuthentication()
        }
    }

    private fun anonymousAuthentication() = EnrichedAuthentication(
        initialAuth = null,
        egressTokenSuppliersByService = tokenSuppliers,
        pid = null,
        isOnBehalf = false
    )

    private fun pidFromHeader(request: HttpServletRequest): String? {
        val header: String? = request.getHeader(CustomHttpHeaders.PID)

        return when {
            !hasLength(header) -> null
            else -> if (header!!.contains(ENCRYPTION_MARK)) pidDecrypter.decrypt(header) else header
        }
    }

    private companion object {
        private const val ENCRYPTION_MARK = "."
    }
}
