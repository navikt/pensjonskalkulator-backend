package no.nav.pensjon.kalkulator.lagring.client.skribenten

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.lagring.LagreSimulering
import no.nav.pensjon.kalkulator.lagring.LagreSimuleringResponse
import no.nav.pensjon.kalkulator.lagring.client.LagreSimuleringClient
import no.nav.pensjon.kalkulator.lagring.client.skribenten.map.OpprettBrevDtoV1Mapper.toDto
import no.nav.pensjon.kalkulator.lagring.api.dto.BrevResponseDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.map.OpprettBrevDtoV1Mapper.fromDto
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
class SkribentenClient(
    @Value("\${skribenten.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), LagreSimuleringClient {
    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override fun lagreSimulering(sakId: Long, lagreSimulering: LagreSimulering): LagreSimuleringResponse {
        val uri = "/sak/$sakId/brev"
        log.debug { "POST to URI: '$uri'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .bodyValue(toDto(lagreSimulering))
                .retrieve()
                .bodyToMono<BrevResponseDtoV1>()
                .retryWhen(retryBackoffSpec(uri))
                .block()!!
                .also { countCalls(MetricResult.OK) }
                .let { fromDto(it) }
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    override fun service(): EgressService = SERVICE

    private fun setHeaders(headers: HttpHeaders) {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(EgressAccess.token(SERVICE).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    private companion object {
        private val SERVICE = EgressService.SKRIBENTEN
    }
}
