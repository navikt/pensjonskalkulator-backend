package no.nav.pensjon.kalkulator.tech.security.egress.oauth2

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config.OAuth2ConfigurationGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.*
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

abstract class OAuth2TokenClient(
    private val webClient: WebClient,
    private val expirationChecker: ExpirationChecker,
    private val oauth2ConfigGetter: OAuth2ConfigurationGetter
) : TokenDataGetter {

    override fun getTokenData(accessParameter: TokenAccessParameter, audience: String): TokenData {
        log.debug("Getting token for audience '{}'...", audience)

        return try {
            val body: OAuth2TokenDto = webClient
                .post()
                .uri(getTokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(prepareTokenRequestBody(accessParameter, audience))
                .retrieve()
                .bodyToMono(OAuth2TokenDto::class.java)
                .block()!!
            // Note: Do not use .body instead of .bodyValue, since this results in chunked encoding,
            // which the endpoint may not support, resulting in 404 Not Found

            log.info("Token obtained for audience '{}'", audience)
            OAuth2TokenDataMapper.map(body, expirationChecker.time())
        } catch (e: WebClientResponseException) {
            throw TokenGetterException("Failed to obtain token: " + e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw TokenGetterException("Failed to obtain token", e)
        }
    }

    private fun getTokenEndpoint(): String = oauth2ConfigGetter.getTokenEndpoint()

    protected fun isExpired(token: TokenData): Boolean =
        expirationChecker.isExpired(token.issuedTime, token.expiresInSeconds)

    protected abstract fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter, audience: String
    ): MultiValueMap<String, String>

    companion object {
        private val log = LoggerFactory.getLogger(OAuth2TokenClient::class.java)
    }
}
