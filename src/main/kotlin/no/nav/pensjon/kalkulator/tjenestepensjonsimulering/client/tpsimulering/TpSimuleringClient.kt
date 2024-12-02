package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering

import io.netty.handler.timeout.ReadTimeoutHandler
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.SimulerTjenestepensjonResponseDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map.TpSimuleringClientMapper
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map.TpSimuleringClientMapper.toDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.netty.http.client.HttpClient

@Component
class TpSimuleringClient(
    @Value("\${tjenestepensjon.simulering.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), TjenestepensjonSimuleringClient {

    private val log = KotlinLogging.logger {}
    private val webClient = webClientBuilder.baseUrl(baseUrl)
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient
                    .create()
                    .doOnConnected { it.addHandlerLast(ReadTimeoutHandler(ON_CONNECTED_READ_TIMEOUT_SECONDS)) })
        ).build()

    override fun hentTjenestepensjonSimulering(request: SimuleringOffentligTjenestepensjonSpec, pid: Pid): OFTPSimuleringsresultat {
        val uri = "/$API_PATH"
        log.debug { "POST to URL: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .bodyValue(toDto(request, pid))
                .headers { setHeaders(it) }
                .retrieve()
                .bodyToMono(SimulerTjenestepensjonResponseDto::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?.let(TpSimuleringClientMapper::fromDto)
                .also { countCalls(MetricResult.OK) } ?: throw EgressException("No response body")
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    override fun service(): EgressService = service

    companion object {
        private const val API_PATH = "v2025/tjenestepensjon/v1/simulering"
        private val service = EgressService.TJENESTEPENSJON_SIMULERING
        private const val ON_CONNECTED_READ_TIMEOUT_SECONDS = 45
    }
}