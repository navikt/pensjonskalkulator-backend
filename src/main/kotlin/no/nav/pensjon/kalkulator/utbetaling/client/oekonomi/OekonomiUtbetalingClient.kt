package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi

import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.utbetaling.Utbetaling
import no.nav.pensjon.kalkulator.utbetaling.client.UtbetalingClient
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.HentUtbetalingerRequestDto
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.OekonomiUtbetalingDto
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.map.UtbetalingMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class OekonomiUtbetalingClient(
    @Value("\${sokos.utbetaldata.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : ExternalServiceClient(retryAttempts), UtbetalingClient {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override suspend fun hentSisteMaanedsUtbetaling(pid: Pid): List<Utbetaling> {
        val uri = "$baseUrl/$HENT_UTBETALINGER_PATH"
        log.debug { "GET from URL: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .bodyValue(HentUtbetalingerRequestDto(pid.value))
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(object: ParameterizedTypeReference<List<OekonomiUtbetalingDto>>() {})
                .retryWhen(retryBackoffSpec(uri))
                .awaitSingle()
                ?.let { UtbetalingMapper.fromDto(it) }
                .also { countCalls(MetricResult.OK) }
                ?: throw EgressException("No utbetaling found")
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    override fun service() = service

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[CustomHttpHeaders.PID] = pid.value
    }

    companion object {
        private const val HENT_UTBETALINGER_PATH = "utbetaldata/api/v2/hent-utbetalingsinformasjon/intern"
        private val service = EgressService.UTBETALING_DATA
    }
}