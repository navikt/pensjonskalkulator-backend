package no.nav.pensjon.kalkulator.opptjening.client.popp

import com.github.benmanes.caffeine.cache.Cache
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.acl.PoppPensjonspoengResult
import no.nav.pensjon.kalkulator.opptjening.client.popp.acl.PoppPensjonspoengSpec
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.cache.CacheConfigurator.createCache
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
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

@Component
class PoppPensjonspoengClient(
    @param:Value($$"${popp.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    cacheManager: CaffeineCacheManager,
    private val traceAid: TraceAid,
    @Value($$"${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), PensjonspoengClient, Pingable {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    private val cache: Cache<Pid, List<AarligOpptjening>> =
        createCache(name = "pensjonspoeng", cacheManager)

    override fun service() = service

    override fun fetchPensjonspoeng(pid: Pid): List<AarligOpptjening> =
        cache.getIfPresent(pid) ?: fetchFreshData(pid).also { cache.put(pid, it) }

    private fun fetchFreshData(pid: Pid): List<AarligOpptjening> {
        val url = "$baseUrl/$HENT_PENSJONSPOENG_PATH"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$HENT_PENSJONSPOENG_PATH")
                .headers(::setHeaders)
                .bodyValue(PoppPensjonspoengSpec(fnr = pid.value))
                .retrieve()
                .bodyToMono<PoppPensjonspoengResult>()
                .retryWhen(retryBackoffSpec(url))
                .block()?.toInternalValue().orEmpty()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val url = "$baseUrl/$PING_PENSJONSPOENG_PATH"

        return try {
            val responseBody = webClient
                .get()
                .uri("/$PING_PENSJONSPOENG_PATH")
                .headers(::setPingHeaders)
                .retrieve()
                .bodyToMono<String>()
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?: ""

            PingResult(service, ServiceStatus.UP, url, responseBody)
        } catch (e: WebClientRequestException) {
            PingResult(service, ServiceStatus.DOWN, url, e.message ?: "forespørsel feilet")
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, url, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val BASE_PATH = "popp/api"
        private const val PENSJONSPOENG_PATH = "$BASE_PATH/pensjonspoeng"
        private const val HENT_PENSJONSPOENG_PATH = "$PENSJONSPOENG_PATH/hent"
        private const val PING_PENSJONSPOENG_PATH = "$PENSJONSPOENG_PATH/ping"
        private val service = EgressService.PENSJONSOPPTJENING
    }
}