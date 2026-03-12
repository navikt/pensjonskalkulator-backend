package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.TilgangResult
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.PopulasjonstilgangClient
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl.ProblemDetailResponseDto
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl.TilgangResultDto
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl.TilgangResultMapper
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import tools.jackson.databind.json.JsonMapper

@Component
class TilgangsmaskinClient(
    @param:Value($$"${tilgangsmaskinen.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    private val jsonMapper: JsonMapper,
    @Value($$"${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), PopulasjonstilgangClient {
    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override fun sjekkTilgang(pid: Pid): TilgangResult {
        val uri = "/$API_RESOURCE"
        log.debug { "POST to URI: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .bodyValue(pid.value)
                .headers(::setHeaders)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(retryBackoffSpec(uri))
                .block()
            countCalls(MetricResult.OK)
            TilgangResultMapper.fromDto(TilgangResultDto.Innvilget)
        } catch (e: WebClientRequestException) {
            log.warn { "Request to tilgangsmaskinen failed: ${e.message}" }
            countCalls(MetricResult.BAD_SERVER)
            feilResultat(begrunnelse = e.message)
        } catch (e: EgressException) {
            handleErrorResponse(e)
        }
    }

    private fun handleErrorResponse(e: EgressException): TilgangResult {
        if (e.statusCode == HttpStatus.FORBIDDEN) {
            return try {
                val problemDetail = jsonMapper.readValue(
                    e.message ?: "",
                    ProblemDetailResponseDto::class.java
                )
                countCalls(MetricResult.OK)
                TilgangResultMapper.fromDto(TilgangResultDto.Avvist(problemDetail))
            } catch (e: Exception) {
                log.warn { "Failed to parse 403 response body: ${e.message}" }
                countCalls(MetricResult.BAD_SERVER)
                feilResultat(begrunnelse = e.message)
            }
        }
        log.error(e) { "Unexpected error from tilgangsmaskinen: ${e.message}" }
        countCalls(MetricResult.BAD_SERVER)
        return feilResultat(begrunnelse = e.message)
    }

    private fun feilResultat(begrunnelse: String?) = TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEILET,
        begrunnelse,
        traceId = null
    )

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    override fun service(): EgressService = service

    companion object {
        private const val API_RESOURCE = "api/v1/komplett"
        private val service = EgressService.TILGANGSMASKINEN
    }
}