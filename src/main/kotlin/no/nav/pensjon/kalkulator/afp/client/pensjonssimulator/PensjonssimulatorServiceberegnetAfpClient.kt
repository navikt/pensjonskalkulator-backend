package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.client.ServiceberegnetAfpClient
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningAfpResultDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map.ServiceberegningAfpResultMapper
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map.ServiceberegningAfpSpecMapper
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class PensjonssimulatorServiceberegnetAfpClient(
    @param:Value("\${pensjonssimulator.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), ServiceberegnetAfpClient {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override fun simulerServiceberegnetAfp(spec: ServiceberegnetAfpSpec): ServiceberegnetAfpResult {
        val url = "$baseUrl/$RESOURCE"
        log.debug { "POST to URL: '$url'" }

        return try {
            val dto = ServiceberegningAfpSpecMapper.toDto(spec)

            webClient
                .post()
                .uri("/$RESOURCE")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono<ServiceberegningAfpResultDto>()
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(ServiceberegningAfpResultMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: throw EgressException("No response body from $url")
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
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

    private companion object {
        private const val RESOURCE = "api/nav/v1/simuler-afp-serviceberegning"
        private val service = EgressService.PENSJONSSIMULATOR
    }
}
