package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.FEATURE_URI
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.ShadowTilgangComparator
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.filter.GenericFilterBean

class ImpersonalAccessFilter(
    private val pidGetter: PidExtractor,
    private val groupMembershipService: GroupMembershipService,
    private val auditor: Auditor,
    private val shadowTilgangComparator: ShadowTilgangComparator
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        // Request for state of feature toggle requires no authentication or access check:
        if ((request as HttpServletRequest).requestURI.startsWith(FEATURE_URI)) {
            chain.doFilter(request, response)
            return
        }

        if (hasPid(request)) {
            val pid = pidGetter.pid()
            val groupResult: Boolean

            try {
                log.warn { "sjekker tilganger for bruker ${pid.displayValue}" }
                groupResult = groupMembershipService.innloggetBrukerHarTilgang(pid)
                shadowTilgangComparator.compareAsync(pid, groupResult)

                if (!groupResult) {
                    forbidden(response as HttpServletResponse)
                    return
                }
            } catch (_: NotFoundException) {
                notFound(response as HttpServletResponse)
                return
            }

            auditor.audit(pid, request.requestURI)
        }

        chain.doFilter(request, response)
    }

    private fun hasPid(request: HttpServletRequest): Boolean =
        hasLength(request.getHeader(CustomHttpHeaders.PID))

    private fun forbidden(response: HttpServletResponse) {
        "Adgang nektet pga. manglende gruppemedlemskap".let {
            log.warn { it }
            response.sendError(HttpStatus.FORBIDDEN.value(), it)
        }
    }

    private fun notFound(response: HttpServletResponse) {
        "Person ikke funnet".let {
            log.info { it }
            response.sendError(HttpStatus.NOT_FOUND.value(), it)
        }
    }
}
