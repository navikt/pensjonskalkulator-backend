package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend

import com.github.benmanes.caffeine.cache.Cache
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.opptjening.behandlet.AarvisBeholdningOgMerknader
import no.nav.pensjon.kalkulator.opptjening.behandlet.client.BeholdningOgMerknadClient
import no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl.OpptjeningAarvisBeholdningOgMerknader
import no.nav.pensjon.kalkulator.person.EncryptedPid
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.cache.CacheConfigurator.createCache
import no.nav.pensjon.kalkulator.tech.crypto.CryptoService
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * Gjør kall til pensjon-selvbetjening-opptjening-backend
 * (som behandler opptjening fra opptjeningsregisteret).
 */
@Component
class BehandletOpptjeningClient(
    @Value($$"${opptjening.url}") baseUrl: String,
    @Value($$"${web-client.retry-attempts}") retryAttempts: String,
    webClientBuilder: WebClient.Builder,
    cacheManager: CaffeineCacheManager,
    private val pidEncrypter: CryptoService,
    private val traceAid: TraceAid,
) : ExternalServiceClient(retryAttempts), BeholdningOgMerknadClient {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    private val cache: Cache<Pid, AarvisBeholdningOgMerknader> =
        createCache(name = "beholdninger-og-merknader", cacheManager)

    override fun fetchBeholdningOgMerknader(pid: Pid): AarvisBeholdningOgMerknader =
        cache.getIfPresent(pid) ?: fetchFreshData(pid).also { cache.put(pid, it) }

    override fun service() = service

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun fetchFreshData(pid: Pid): AarvisBeholdningOgMerknader {
        val uri = PATH

        return try {
            webClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers { setHeaders(it, encryptedPid(pid)) }
                .retrieve()
                .bodyToMono<OpptjeningAarvisBeholdningOgMerknader>()
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?.toInternalValue()
                ?: AarvisBeholdningOgMerknader(perAar = emptyMap())
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    /**
     * PID is used in an HTTP header, so it must be encrypted.
     */
    private fun encryptedPid(pid: Pid) =
        EncryptedPid(pidEncrypter.encrypt(pid.value))

    private fun setHeaders(headers: HttpHeaders, pid: EncryptedPid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[CustomHttpHeaders.PERSON_ID] = pid.value
    }

    private companion object {
        private const val PATH = "/api/v1/beholdninger-og-merknader"

        private val service = EgressService.OPPTJENING_SELVBETJENING
    }
}