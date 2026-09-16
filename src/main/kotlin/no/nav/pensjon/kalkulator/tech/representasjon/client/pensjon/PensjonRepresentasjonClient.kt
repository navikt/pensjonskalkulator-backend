package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon

import com.github.benmanes.caffeine.cache.Cache
import no.nav.pensjon.kalkulator.common.client.PingableServiceClient
import no.nav.pensjon.kalkulator.tech.cache.CacheConfigurator.createCache
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonSpec
import no.nav.pensjon.kalkulator.tech.representasjon.client.RepresentasjonClient
import no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto.PensjonRepresentasjonResult
import no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto.PensjonRepresentasjonSpec
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
import java.time.Duration
import java.time.temporal.ChronoUnit.HOURS

/**
 * Client for accessing the 'pensjon-representasjon' service
 * (see github.com/navikt/pensjon-fullmakt)
 */
@Component
class PensjonRepresentasjonClient(
    @param:Value($$"${pensjon-representasjon.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    cacheManager: CaffeineCacheManager,
    private val traceAid: TraceAid,
    @Value($$"${web-client.retry-attempts}") retryAttempts: String
) : PingableServiceClient(null, webClientBuilder, retryAttempts),
    RepresentasjonClient {

    private val cache: Cache<RepresentasjonSpec, Representasjon> =
        createCache(name = "representasjon", manager = cacheManager, expiry = Duration.of(1, HOURS))

    override fun fetchRepresentasjon(spec: RepresentasjonSpec): Representasjon =
        cache.getIfPresent(spec) ?: fetchFreshRepresentasjon(spec).also { cache.put(spec, it) }

    private fun fetchFreshRepresentasjon(spec: RepresentasjonSpec): Representasjon {
        val uri = "$baseUrl/$PATH"

        return try {
            webClient
                .post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(PensjonRepresentasjonSpec.from(spec))
                .retrieve()
                .bodyToMono<PensjonRepresentasjonResult>()
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?.toInternalValue()
                .also { countCalls(MetricResult.OK) }
                ?: noRepresentasjonForhold()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun service(): EgressService = service

    override fun pingPath(): String = "$baseUrl/actuator/health/liveness"

    override fun setPingHeaders(headers: HttpHeaders) {
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val PATH = "/representasjon/hasValidRepresentasjonsforhold"

        private val service = EgressService.PENSJON_REPRESENTASJON

        private fun noRepresentasjonForhold() =
            Representasjon(isValid = false, fullmaktsgiver = null)
    }
}