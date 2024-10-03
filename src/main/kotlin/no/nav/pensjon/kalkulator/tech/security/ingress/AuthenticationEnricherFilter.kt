package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.filter.GenericFilterBean

/**
 * Servlet filter which augments Spring Security authentication data as required by the application.
 */
class AuthenticationEnricherFilter(private val enricher: SecurityContextEnricher) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            enricher.enrichAuthentication(request as HttpServletRequest, response as HttpServletResponse)
        } catch (e: AccessDeniedException) {
            log.warn { "Access denied - ${e.message}" }
            (response as HttpServletResponse).sendError(HttpStatus.FORBIDDEN.value(), e.message)
            return
        }

        chain.doFilter(request, response)
    }
}
