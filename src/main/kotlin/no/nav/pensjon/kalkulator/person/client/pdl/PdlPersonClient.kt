package no.nav.pensjon.kalkulator.person.client.pdl

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import no.nav.pensjon.kalkulator.person.client.pdl.map.PersonMapper
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Component
class PdlPersonClient(
    @Value("\${persondata.url}") private val baseUrl: String,
    private val webClient: WebClient,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), PersonClient, Pingable {

    override fun service() = service

    override fun fetchPerson(pid: Pid): Person? {
        val uri = "$baseUrl/$PATH"
        log.debug { "POST to URI: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .bodyValue(query(pid))
                .retrieve()
                .bodyToMono(PersonResponseDto::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?.also {
                    log.warn { warnings(it) }
                    countCalls(MetricResult.OK)
                }
                ?.let(PersonMapper::fromDto)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val uri = "$baseUrl/$PATH"

        return try {
            webClient
                .options()
                .uri(uri)
                .headers(::setHeaders)
                .retrieve()
                .toBodilessEntity()
                .block()

            PingResult(service, ServiceStatus.UP, uri, "Ping OK")
        } catch (e: WebClientResponseException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        } catch (e: RuntimeException) { // e.g. when connection broken
            PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "Ping failed")
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.BEHANDLINGSNUMMER] = BEHANDLINGSNUMMER
        headers[CustomHttpHeaders.THEME] = THEME
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val PATH = "graphql"
        private const val THEME = "PEN"

        // https://behandlingskatalog.nais.adeo.no/process/team/d55cc783-7850-4606-9ff6-1fc44b646c9d/91a4e540-5e39-4c10-971f-49b48f35fe11
        private const val BEHANDLINGSNUMMER = "B353"
        private val service = EgressService.PERSONDATALOESNINGEN

        private fun query(pid: Pid) = """{
	"query": "query(${"$"}ident: ID!) { hentPerson(ident: ${"$"}ident) { navn(historikk: false) { fornavn }, foedsel { foedselsdato }, sivilstand(historikk: false) { type } } }",
	"variables": {
		"ident": "${pid.value}"
	}
}"""

        private fun warnings(response: PersonResponseDto) =
            response.extensions?.warnings?.joinToString {
                (it.message ?: "-") + " (${warningDetails(it.details)})"
            } ?: ""

        private fun warningDetails(details: Any?) =
            when (details) {
                is String -> details
                is LinkedHashMap<*, *> -> (details["missing"] as ArrayList<*>).joinToString()
                else -> details.toString()
            }
    }
}
