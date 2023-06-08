package no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.AuthorizationGrantType
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterNames
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto.SamlTokenDataDto
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

/**
 * Fetches SAML tokens from Gandalf Security Token Service (STS).
 */
@Component
class GandalfSamlTokenClient(
    @Value("\${sts.url}") private val baseUrl: String,
    private val webClient: WebClient
) : SamlTokenClient, Pingable {
    private val log = KotlinLogging.logger {}

    override fun fetchSamlToken(): SamlTokenDataDto {
        val uri = "$baseUrl$TOKEN_EXCHANGE_PATH"
        log.debug { "GET from URI: '$uri'" }
        val idToken = SecurityContextHolder.getContext().authentication.credentials as Jwt

        try {
            val response = webClient
                .post()
                .uri(uri)
                .headers(::setHeaders)
                .body(body(idToken))
                .retrieve()
                .bodyToMono(SamlTokenDataDto::class.java)
                .block()
                ?: emptyDto()

            return response
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
        private const val TOKEN_TYPE = "urn:ietf:params:oauth:token-type:access_token"
        private const val TOKEN_EXCHANGE_PATH = "/rest/v1/sts/token/exchange"
        private const val PING_PATH = "/ping" //TODO
        private val service = EgressService.SAML_TOKEN

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun setPingHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun body(idToken: Jwt) =
            BodyInserters
                .fromFormData(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.TOKEN_EXCHANGE.value)
                .with(OAuth2ParameterNames.SUBJECT_TOKEN_TYPE, TOKEN_TYPE)
                .with(OAuth2ParameterNames.SUBJECT_TOKEN, idToken.tokenValue)

        private fun callId() = UUID.randomUUID().toString()

        private fun emptyDto() = SamlTokenDataDto("", "", "", 0)
    }
}
