package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.TilgangResult
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ShadowTilgangComparator(
    private val tilgangService: TilgangService,
    private val navIdExtractor: SecurityContextNavIdExtractor
) : DisposableBean {

    private val log = KotlinLogging.logger {}
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tilgangCache: Cache<String, Deferred<TilgangResult>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build()

    fun compareAsync(pid: Pid, groupMembershipResult: Boolean) {
        // Capture security context on request thread before launching
        val securityContext = SecurityCoroutineContext()

        scope.launch(securityContext) {
            try {
                val navIdent = navIdExtractor.id()

                val result = tilgangCache.get("$navIdent:${pid.value}") {
                    scope.async(securityContext) {
                        tilgangService.sjekkTilgang(pid)
                    }
                }.await()

                logIfMismatch(pid, groupMembershipResult, result)
            } catch (e: Exception) {
                log.warn { "Shadow tilgang check failed: ${e.message}" }
            }
        }
    }

    private fun logIfMismatch(pid: Pid, groupMembershipResult: Boolean, tilgangResult: TilgangResult) {
        if (tilgangResult.innvilget != groupMembershipResult) {
            val avvisningsDetaljer = if (!tilgangResult.innvilget)
                " (kode=${tilgangResult.avvisningAarsak}, begrunnelse=${tilgangResult.begrunnelse}, " +
                        "traceId=${tilgangResult.traceId})"
            else ""
            log.warn {
                "Tilgang mismatch for person ${pid.displayValue}: " +
                        "groupMembership=$groupMembershipResult, tilgangService=${tilgangResult.innvilget}$avvisningsDetaljer"
            }
        }
    }

    override fun destroy() {
        scope.cancel()
    }
}
