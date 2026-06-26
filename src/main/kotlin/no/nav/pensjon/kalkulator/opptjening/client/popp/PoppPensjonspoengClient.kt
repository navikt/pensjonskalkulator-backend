package no.nav.pensjon.kalkulator.opptjening.client.popp

import com.github.benmanes.caffeine.cache.Cache
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.opptjening.AarligBeholdning
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengRequestDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PensjonspoengResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PoppBeholdningSpec
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.PoppBeholdningResult
import no.nav.pensjon.kalkulator.opptjening.client.popp.map.BeholdningMapper
import no.nav.pensjon.kalkulator.opptjening.client.popp.map.PensjonspoengMapper
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
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

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

    private val cache: Cache<Pid, Pair<List<AarligOpptjening>, List<AarligBeholdning>>> =
        createCache("opptjening", cacheManager)

    override fun service() = service

    override fun fetchOpptjeningOgBeholdning(pid: Pid): Pair<List<AarligOpptjening>, List<AarligBeholdning>> =
        cache.getIfPresent(pid) ?: fetchFreshOpptjeningOgBeholdning(pid).also { cache.put(pid, it) }

    private fun fetchFreshOpptjeningOgBeholdning(pid: Pid): Pair<List<AarligOpptjening>, List<AarligBeholdning>> =
        Mono.zip(
            fetchOpptjening(pid),
            fetchBeholdninger(pid)
        ).block()
            ?.let(::asListPair)
            ?: emptyListPair()

    private fun fetchBeholdninger(pid: Pid): Mono<PoppBeholdningResult> {
        val url = "$baseUrl/$BEHOLDNING_PATH"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$BEHOLDNING_PATH")
                .headers { setHeaders(it, pid) }
                .bodyValue(PoppBeholdningSpec(fnr = pid.value))
                .retrieve()
                .bodyToMono<PoppBeholdningResult>()
                .retryWhen(retryBackoffSpec(url))
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    private fun fetchOpptjening(pid: Pid): Mono<PensjonspoengResponseDto> {
        val url = "$baseUrl/$OPPTJENING_PATH"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri("/$OPPTJENING_PATH")
                .headers { setHeaders(it, pid) }
                .bodyValue(PensjonspoengRequestDto(fnr = pid.value))
                .retrieve()
                .bodyToMono<PensjonspoengResponseDto>()
                .retryWhen(retryBackoffSpec(url))
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val url = "$baseUrl/$PING_PATH"

        return try {
            val responseBody = webClient
                .get()
                .uri("/$PING_PATH")
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

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[CustomHttpHeaders.PID] = pid.value
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val BASE_PATH = "popp/api"
        private const val BEHOLDNING_PATH = "$BASE_PATH/beholdning"
        private const val OPPTJENING_PATH = "$BASE_PATH/pensjonspoeng/hent"
        private const val PING_PATH = "$BASE_PATH/pensjonspoeng/ping"
        private val service = EgressService.PENSJONSOPPTJENING

        private fun asListPair(
            responser: Tuple2<PensjonspoengResponseDto, PoppBeholdningResult>
        ): Pair<List<AarligOpptjening>, List<AarligBeholdning>> =
            Pair(
                PensjonspoengMapper.fromDto(responser.t1),
                BeholdningMapper.fromDto(responser.t2)
            )

        private fun emptyListPair(): Pair<List<AarligOpptjening>, List<AarligBeholdning>> =
            Pair(emptyList(), emptyList())
    }
}