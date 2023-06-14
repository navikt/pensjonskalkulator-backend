package no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.UsernameTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw.dto.UsernameTokenDto
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

/**
 * Fetches UsernameToken (UNT) from FSS gateway.
 */
@Component
class FssGatewayUsernameTokenClient(
    @Value("\${unt.url}") private val baseUrl: String,
    private val webClient: WebClient
) : UsernameTokenClient, Pingable {
    private val log = KotlinLogging.logger {}

    override fun fetchUsernameToken(): UsernameTokenDto {
        val uri = "$baseUrl$TOKEN_PATH"
        log.debug { "GET from URI: '$uri'" }

        try {
            val token = webClient
                .get()
                .uri(uri)
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""
            return UsernameTokenDto(token)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to GET $uri: ${e.message}", e)
        }
    }

    override fun ping(): PingResult {
        val uri = baseUrl + PING_PATH

        try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .headers(::setPingHeaders)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""

            return PingResult(service, ServiceStatus.UP, uri, responseBody)
        } catch (e: WebClientResponseException) {
            return PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        } catch (e: RuntimeException) { // e.g. when connection broken
            return PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "Ping failed")
        }
    }

    companion object {
        private const val TOKEN_PATH = "/ws-support/unt"
        private const val PING_PATH = "/ping" //TODO
        private val service = EgressService.USERNAME_TOKEN

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun setPingHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun callId() = UUID.randomUUID().toString()
    }
}
