package no.nav.pensjon.kalkulator.person.client.pdl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Land
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import no.nav.pensjon.kalkulator.person.client.pdl.map.PersonMapper
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDate
import java.util.*

@Component
class PdlPersonClient(
    @Value("\${pdl.url}") private val baseUrl: String,
    private val webClient: WebClient
) : PersonClient, Pingable {
    private val log = KotlinLogging.logger {}

    override fun getPerson(pid: Pid): Person {
        val uri = baseUrl + PERSON_PATH
        log.debug { "POST to URI: '$uri'" }

        return try {
            webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .bodyValue(query(pid))
                .retrieve()
                .bodyToMono(PersonResponseDto::class.java)
                .block()
                ?.let(PersonMapper::fromDto)
                ?: emptyPerson()
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to GET $uri: ${e.message}", e)
        }
    }

    override fun ping(): PingResult {
        val uri = baseUrl + PING_PATH

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

    companion object {
        private const val PERSON_PATH = "/graphql"
        private const val PING_PATH = "/graphql"
        private const val BEHANDLINGSNUMMER = "B353" // https://behandlingskatalog.nais.adeo.no/process/team/d55cc783-7850-4606-9ff6-1fc44b646c9d/91a4e540-5e39-4c10-971f-49b48f35fe11
        private const val THEME = "PEN"
        private val service = EgressService.PERSONDATA

        private fun query(pid: Pid) = """{
	"query": "query(${"$"}ident: ID!) { hentPerson(ident: ${"$"}ident) { navn(historikk: false) { fornavn }, foedsel { foedselsdato }, statsborgerskap { land }, sivilstand(historikk: true) { type } } }",
	"variables": {
		"ident": "${pid.value}"
	}
}"""

        private fun setHeaders(headers: HttpHeaders) {
            headers.contentType = MediaType.APPLICATION_JSON
            headers.accept = listOf(MediaType.APPLICATION_JSON)
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[CustomHttpHeaders.BEHANDLINGSNUMMER] = BEHANDLINGSNUMMER
            headers[CustomHttpHeaders.THEME] = THEME
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun callId() = UUID.randomUUID().toString()

        private fun emptyPerson() = Person("", LocalDate.MIN, Land.OTHER, Sivilstand.UOPPGITT)
    }
}
