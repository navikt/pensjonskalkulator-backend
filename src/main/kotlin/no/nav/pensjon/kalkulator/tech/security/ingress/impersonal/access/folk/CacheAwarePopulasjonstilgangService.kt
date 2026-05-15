package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.*
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CacheAwarePopulasjonstilgangService(
    private val navIdExtractor: SecurityContextNavIdExtractor,
    private val populasjonstilgangService: PopulasjonstilgangService
) {
    private val log = KotlinLogging.logger {}
    private val tilgangCache: Cache<String, Deferred<TilgangResult>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun eventuellTilgangsnektAarsak(pid: Pid): String? =
        try {
            with(populasjonstilgang(pid)) {
                if (innvilget) null
                else avvisningsinfo
            }
        } catch (e: Exception) {
            "${AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEILET}: ${e.message}".also { log.error(e) { it } }
        }

    private fun populasjonstilgang(pid: Pid): TilgangResult {
        val deferred = tilgangCache.get(cacheKey(pid)) {
            scope.async(SecurityCoroutineContext()) {
                populasjonstilgangService.sjekkTilgang(pid)
            }
        }

        return runBlocking { deferred.await() }
    }

    private fun cacheKey(pid: Pid): String =
        "${navIdExtractor.id()}:${pid.value}"
}