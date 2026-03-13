package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.*
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.FEATURE_URI
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.fag.FagtilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.PopulasjonstilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.TilgangResult
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.filter.GenericFilterBean
import java.util.concurrent.TimeUnit

class ImpersonalAccessFilter(
    private val pidGetter: PidExtractor,
    private val navIdExtractor: SecurityContextNavIdExtractor,
    private val fagtilgangService: FagtilgangService,
    private val populasjonstilgangService: PopulasjonstilgangService,
    private val auditor: Auditor
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}
    private val tilgangCache: Cache<String, Deferred<TilgangResult>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            // Request for state of feature toggle requires no authentication or access check:
            if ((request as HttpServletRequest).requestURI.startsWith(FEATURE_URI)) {
                chain.doFilter(request, response)
                return
            }

            if (hasPid(request)) {
                eventuellTilgangsnektAarsak()?.let {
                    forbidden(response, aarsak = it)
                    return
                }

                auditor.audit(onBehalfOfPid = pidGetter.pid(), requestUri = request.requestURI)
            }

            chain.doFilter(request, response)
        } catch (e: Exception) {
            // Enhver feil skal gi 'tilgang avvist'
            forbidden(response, aarsak = "feil i tilgangssjekk", e)
        }
    }

    private fun eventuellTilgangsnektAarsak(): String? =
        if (fagtilgangService.tilgangInnvilget())
            eventuellPopulasjonstilgangsnektAarsak()
        else
            "manglende faggruppemedlemskap"

    private fun eventuellPopulasjonstilgangsnektAarsak(): String? =
        with(populasjonstilgang(pid = pidGetter.pid())) {
            if (innvilget) null
            else avvisningsinfo
        }

    private fun populasjonstilgang(pid: Pid): TilgangResult {
        val deferred = tilgangCache.get(cacheKey(navIdent = navIdExtractor.id(), pid)) {
            scope.async(SecurityCoroutineContext()) {
                populasjonstilgangService.sjekkTilgang(pid)
            }
        }

        return runBlocking { deferred.await() }
    }

    private fun cacheKey(navIdent: String, pid: Pid): String =
        "$navIdent:${pid.value}"

    private fun hasPid(request: HttpServletRequest): Boolean =
        hasLength(request.getHeader(CustomHttpHeaders.PID))

    private fun forbidden(response: ServletResponse, aarsak: String, e: Exception? = null) {
        "Tilgang nektet pga. $aarsak".let {
            if (e == null) {
                log.warn { it }
                respondForbidden(response, aarsak = it)
            } else {
                log.error(e) { "$it - ${e.message}" }
                respondForbidden(response, aarsak = "$it - se logg for detaljer")
            }
        }
    }

    private fun respondForbidden(response: ServletResponse, aarsak: String) {
        (response as HttpServletResponse).sendError(HttpStatus.FORBIDDEN.value(), aarsak)
    }
}
