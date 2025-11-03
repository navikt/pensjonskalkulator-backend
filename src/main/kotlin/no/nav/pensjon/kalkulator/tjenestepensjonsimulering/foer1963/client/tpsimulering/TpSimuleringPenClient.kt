package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.OffentligTjenestepensjonSimuleringFoer1963Resultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.SimuleringOffentligTjenestepensjonFoer1963Spec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.TjenestepensjonSimuleringFoer1963Client
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.dto.SimulerTjenestepensjonFoer1963ResponseDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.map.TpSimuleringFoer1963ClientMapper.toDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.map.TpSimuleringFoer1963ClientMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.netty.http.client.HttpClient

@Component
class TpSimuleringPenClient(
    @param:Value("\${pen.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), TjenestepensjonSimuleringFoer1963Client {

    private val log = KotlinLogging.logger {}
    private val webClient = webClientBuilder.baseUrl(baseUrl)
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient
                    .create()
                    .doOnConnected { it.addHandlerLast(ReadTimeoutHandler(ON_CONNECTED_READ_TIMEOUT_SECONDS)) })
        ).build()

    override fun hentTjenestepensjonSimulering(request: SimuleringOffentligTjenestepensjonFoer1963Spec, pid: Pid): OffentligTjenestepensjonSimuleringFoer1963Resultat {
        val uri = "/$API_PATH"
        log.debug { "POST to URL: '$uri'" }

        val dto = toDto(request, pid)
        log.debug { dto }
        try {
            val rawJson = webClient.post()
                .uri(uri)
                .bodyValue(dto)
                .headers { setHeaders(it) }
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()

            println("Response JSON: $rawJson")

            val responseDto = jacksonObjectMapper().readValue(rawJson, SimulerTjenestepensjonFoer1963ResponseDto::class.java)
            return TpSimuleringFoer1963ClientMapper.fromDto(responseDto)
                .also { countCalls(MetricResult.OK) }
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
        private const val API_PATH = "api/simuler/tjenestepensjon"
        private val service = EgressService.PENSJONSFAGLIG_KJERNE
        private const val ON_CONNECTED_READ_TIMEOUT_SECONDS = 45
    }

}
