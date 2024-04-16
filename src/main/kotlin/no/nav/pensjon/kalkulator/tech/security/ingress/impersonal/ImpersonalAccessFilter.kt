package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.isImpersonal
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class ImpersonalAccessFilter(
    private val pidGetter: PidExtractor,
    private val groupMembershipService: GroupMembershipService,
    private val auditor: Auditor
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (isImpersonal(request as HttpServletRequest)) {
            val pid = pidGetter.pid()

            if (!groupMembershipService.innloggetBrukerHarTilgang(pid)) {
                forbidden(response as HttpServletResponse)
                return
            }

            auditor.audit(pid, request.requestURI)
        }

        chain.doFilter(request, response)
    }

    private fun forbidden(response: HttpServletResponse) {
        "Adgang nektet pga. manglende gruppemedlemskap".let {
            log.warn { it }
            response.sendError(HttpStatus.FORBIDDEN.value(), it)
        }
    }
}
