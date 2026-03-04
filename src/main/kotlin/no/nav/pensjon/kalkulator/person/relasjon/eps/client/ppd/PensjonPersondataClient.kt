package no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.EpsClient
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl.FamilierelasjonDto
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl.FamilierelasjonMapper
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl.PersonaliaTypeDto
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl.RelasjonstypeDto
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
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * Client for accessing the 'pensjon-persondata' service (see github.com/navikt/pensjon-persondata)
 */
@Component
class PensjonPersondataClient(
    @param:Value($$"${pensjon-persondata.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value($$"${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), EpsClient, Pingable {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    override fun service() = service

    override fun fetchNaavaerendeEps(
        soekerPid: Pid,
        personaliaSpec: List<PersonaliaType>
    ): Familierelasjon {
        val uri = "/$NAAVAERENDE_EPS_PATH"
        val url = "${baseUrl}uri"
        val body: List<String> = personaliaSpec.map(PersonaliaTypeDto::externalValue)

        return try {
            webClient
                .post()
                .uri(uri)
                .headers { setHeaders(headers = it, soekerPid) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono<FamilierelasjonDto>()
                .retryWhen(retryBackoffSpec(url))
                .block()?.let(FamilierelasjonMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: emptyFamilierelasjon
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun fetchNyligsteEps(
        soekerPid: Pid,
        sivilstatus: Sivilstatus,
        personaliaSpec: List<PersonaliaType>
    ): Familierelasjon {
        val relasjonstype = RelasjonstypeDto.fromSivilstatus(sivilstatus)
        val uri = "/$NYLIGSTE_EPS_PATH?epsType=${relasjonstype.name}"
        val url = "${baseUrl}uri"
        val body: List<String> = personaliaSpec.map(PersonaliaTypeDto::externalValue)

        return try {
            webClient
                .post()
                .uri(uri)
                .headers { setHeaders(headers = it, soekerPid) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono<FamilierelasjonDto>()
                .retryWhen(retryBackoffSpec(url))
                .block()?.let(FamilierelasjonMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: emptyFamilierelasjon
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

    override fun toString(e: EgressException, uri: String) =
        "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        headers[CustomHttpHeaders.PERSON_ID] = pid.value
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    companion object {
        private const val NAAVAERENDE_EPS_PATH = "api/familierelasjoner/currentEps"
        private const val NYLIGSTE_EPS_PATH = "api/familierelasjoner/mostRecentEps"
        private const val PING_PATH = "TBD" //TODO
        private val service = EgressService.PENSJON_PERSONDATA

        private val emptyFamilierelasjon =
            Familierelasjon(
                pid = null,
                fom = null,
                relasjonstype = Relasjonstype.UKJENT,
                relasjonPersondata = null
            )
    }
}
