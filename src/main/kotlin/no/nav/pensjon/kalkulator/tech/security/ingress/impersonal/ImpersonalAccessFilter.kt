package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.FEATURE_URI
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.TilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.TilgangResult
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.beans.factory.DisposableBean
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.filter.GenericFilterBean
import java.util.concurrent.TimeUnit

@Component
class ImpersonalAccessFilter(
    private val pidGetter: PidExtractor,
    private val groupMembershipService: GroupMembershipService,
    private val auditor: Auditor,
    private val tilgangService: TilgangService,
    private val navIdExtractor: SecurityContextNavIdExtractor
) : GenericFilterBean(), DisposableBean {

    private val log = KotlinLogging.logger {}
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tilgangCache: Cache<String, TilgangResult> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build()

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
                groupResult = groupMembershipService.innloggetBrukerHarTilgang(pid)
                compareTilgang(pid, groupResult)

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

    private fun compareTilgang(pid: Pid, groupResult: Boolean) {
        val navIdent = navIdExtractor.id()
        val key = "$navIdent:${pid.value}"
        val cached = tilgangCache.getIfPresent(key)

        if (cached != null) {
            // Use cached result for comparison
            logIfMismatch(pid, groupResult, cached)
        } else {
            // Fetch fresh result asynchronously
            scope.launch(SecurityCoroutineContext()) {
                try {
                    val tilgangResult = tilgangService.sjekkTilgang(pid)
                    tilgangCache.put(key, tilgangResult)
                    logIfMismatch(pid, groupResult, tilgangResult)
                } catch (e: Exception) {
                    log.warn { "Shadow tilgang check failed: ${e.message}" }
                }
            }
        }
    }

    private fun logIfMismatch(pid: Pid, groupResult: Boolean, tilgangResult: TilgangResult) {
        if (tilgangResult.innvilget != groupResult) {
            val avvisningsDetaljer = if (!tilgangResult.innvilget)
                " (kode=${tilgangResult.avvisningAarsak}, begrunnelse=${tilgangResult.begrunnelse}, " +
                        "traceId=${tilgangResult.traceId})"
            else ""
            log.warn {
                "Tilgang mismatch for person ${pid.displayValue}: " +
                    "groupMembership=$groupResult, tilgangService=${tilgangResult.innvilget}$avvisningsDetaljer"
            }
        }
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

    override fun destroy() {
        scope.cancel()
    }
}
