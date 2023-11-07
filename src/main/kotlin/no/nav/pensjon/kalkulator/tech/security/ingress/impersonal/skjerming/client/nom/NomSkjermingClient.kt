package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.nom

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.PingableServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.SkjermingClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Client for accessing the 'skjermede-personer-pip' service
 * (see https://github.com/navikt/skjerming/tree/main/apps/skjermede-personer-pip)
 */
@Component
class NomSkjermingClient(
    @Value("\${skjermede-personer.url}") private val baseUrl: String,
    private val webClient: WebClient,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : PingableServiceClient(baseUrl, webClient, traceAid, retryAttempts),
    SkjermingClient {

    private val log = KotlinLogging.logger {}

    override fun pingPath(): String = path("dummy")

    override fun service(): EgressService = service

    override fun personErTilgjengelig(pid: Pid): Boolean {
        val uri = "$baseUrl/${path(pid.value)}"
        log.debug { "GET from URI: '$uri'" }

        try {
            val erSkjermet = webClient
                .get()
                .uri(uri)
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(Boolean::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                .also { countCalls(MetricResult.OK) }
                ?: true

            return !erSkjermet
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun setPingHeaders(headers: HttpHeaders) {
        setHeaders(headers)
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun path(skjermetObjectId: String) = "$API_RESOURCE?$OBJECT_TYPE=$skjermetObjectId"

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val API_RESOURCE = "skjermet"
        private const val OBJECT_TYPE = "personident"

        private val service = EgressService.SKJERMEDE_PERSONER
    }
}
