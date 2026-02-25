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
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.FEATURE_URI
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.TilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.TilgangResult
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.filter.GenericFilterBean
import java.util.concurrent.TimeUnit

class ImpersonalAccessFilter(
    private val pidGetter: PidExtractor,
    private val navIdExtractor: SecurityContextNavIdExtractor,
    private val tilgangService: TilgangService,
    private val auditor: Auditor,
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}
    private val tilgangCache: Cache<String, Deferred<TilgangResult>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        // Request for state of feature toggle requires no authentication or access check:
        if ((request as HttpServletRequest).requestURI.startsWith(FEATURE_URI)) {
            chain.doFilter(request, response)
            return
        }
        if (hasPid(request)) {
            val pid = pidGetter.pid()
            val navIdent = navIdExtractor.id()
            val cacheKey = "$navIdent:${pid.value}"
            val securityContext = SecurityCoroutineContext()
            try {
                val deferred = tilgangCache.get(cacheKey) {
                    scope.async(securityContext) {
                        tilgangService.sjekkTilgang(pid)
                    }
                }

                val tilgang = runBlocking { deferred.await() }

                if (!tilgang.innvilget) {
                    log.debug { "Tilgang avvist for: $cacheKey" }
                    forbidden(response as HttpServletResponse, tilgang)
                    return
                }
            } catch (e: Exception) {
                val msg = "Feil fra tilgangsmaskin"
                log.error(e) { "$msg: ${e.message}" }
                forbidden(response as HttpServletResponse, msg)
                return
            }
            auditor.audit(pid, request.requestURI)
        }

        chain.doFilter(request, response)
    }

    private fun hasPid(request: HttpServletRequest): Boolean =
        hasLength(request.getHeader(CustomHttpHeaders.PID))

    private fun forbidden(response: HttpServletResponse, nektetTilgangDetaljer: TilgangResult) {
        forbidden(response, "${nektetTilgangDetaljer.avvisningAarsak}:${nektetTilgangDetaljer.begrunnelse}")
    }

    private fun forbidden(response: HttpServletResponse, msg: String) {
        "Adgang nektet pga. $msg".let {
            log.warn { it }
            response.sendError(HttpStatus.FORBIDDEN.value(), it)
        }
    }
}
