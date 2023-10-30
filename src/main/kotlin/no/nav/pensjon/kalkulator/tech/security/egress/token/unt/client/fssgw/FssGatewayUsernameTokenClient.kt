package no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.UsernameTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw.dto.UsernameTokenDto
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

/**
 * Fetches UsernameToken (UNT) from FSS gateway.
 */
//@Component <--- FssGatewayUsernameTokenClient is not used at the moment
class FssGatewayUsernameTokenClient(
    @Value("\${proxy.url}") private val baseUrl: String,
    private val webClient: WebClient,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), UsernameTokenClient, Pingable {

    private val log = KotlinLogging.logger {}

    override fun service() = service

    override fun fetchUsernameToken(): UsernameTokenDto {
        val uri = "$baseUrl$TOKEN_PATH"
        log.debug { "GET from URI: '$uri'" }

        return try {
            webClient
                .get()
                .uri(uri)
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?.let { UsernameTokenDto(it) }
                .also { countCalls(MetricResult.OK) }
                ?: UsernameTokenDto("")
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun ping(): PingResult {
        val uri = baseUrl + PING_PATH

        return try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .headers(::setPingHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""

            return PingResult(service, ServiceStatus.UP, uri, responseBody)
        } catch (e: WebClientRequestException) {
            PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "forespørsel feilet")
        } catch (e: WebClientResponseException) {
             PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        }
    }

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    private fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    companion object {
        private const val TOKEN_PATH = "/ws-support/unt"
        private const val PING_PATH = "/ping" //TODO
        private val service = EgressService.FSS_GATEWAY
    }
}
