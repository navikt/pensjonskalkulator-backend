package no.nav.pensjon.kalkulator.avtale.client.np.rest

import com.github.benmanes.caffeine.cache.Cache
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonResult
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonResultMapper
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonSpecMapper
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.cache.CacheConfigurator.createCache
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.webclient.autoconfigure.WebClientSsl
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component("norsk-pensjon-rest")
class NorskPensjonRestClient(
    @param:Value($$"${norsk-pensjon.rest.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    ssl: WebClientSsl,
    cacheManager: CaffeineCacheManager,
    private val traceAid: TraceAid,
    @Value($$"${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), PensjonsavtaleClient {

    private val webClient = webClientBuilder.baseUrl(baseUrl)
        .apply(ssl.fromBundle("norsk-pensjon"))
        .build()
    private val log = KotlinLogging.logger {}

    private val cache: Cache<Pid, Pensjonsavtaler> =
        createCache("pensjonsavtaler", cacheManager)

    override fun service() = service

    override fun fetchAvtaler(spec: PensjonsavtaleSpec, pid: Pid): Pensjonsavtaler =
        cache.getIfPresent(pid) ?: fetchFreshAvtaler(spec, pid) //.also { cache.put(pid, it) }

    private fun fetchFreshAvtaler(spec: PensjonsavtaleSpec, pid: Pid): Pensjonsavtaler {
        val url = "$baseUrl/$BEREGN_PATH"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$BEREGN_PATH")
                .headers { setHeaders(it) }
                .bodyValue(NorskPensjonSpecMapper.toDto(spec, pid))
                .retrieve()
                .bodyToMono<NorskPensjonResult>()
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(NorskPensjonResultMapper::fromDto)
                ?: Pensjonsavtaler(
                    avtaler = emptyList(),
                    utilgjengeligeSelskap = emptyList()
                )
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers[HttpHeaders.ACCEPT] = MediaType.APPLICATION_JSON_VALUE
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.NORSK_PENSJON_CORRELATION_ID] = traceAid.callId()
        headers[CustomHttpHeaders.ORGANIZATION_NUMBER] = ORGANISASJONSNUMMER
    }

    companion object {
        private const val BASE_PATH = "pensjonsportal-v1"
        private const val BEREGN_PATH = "$BASE_PATH/beregn"
        private const val ORGANISASJONSNUMMER = "889640782" // ARBEIDS- OG VELFERDSETATEN
        private val service = EgressService.NORSK_PENSJON_REST
    }
}