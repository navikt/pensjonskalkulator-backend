package no.nav.pensjon.kalkulator.tech.security.egress.oauth2

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config.OAuth2ConfigurationGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenData
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenDataGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

abstract class OAuth2TokenClient(
    private val webClient: WebClient,
    private val expirationChecker: ExpirationChecker,
    private val oauth2ConfigGetter: OAuth2ConfigurationGetter,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), TokenDataGetter {

    override fun service() = service

    override fun getTokenData(accessParameter: TokenAccessParameter, audience: String): TokenData {
        log.debug { "Fetching token for audience '$audience'" }
        val uri = getTokenEndpoint()

        return try {
            val body: OAuth2TokenDto = webClient
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(prepareTokenRequestBody(accessParameter, audience))
                .retrieve()
                .bodyToMono(OAuth2TokenDto::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()!!
                .also { countCalls(MetricResult.OK) }
            // Note: Do not use .body instead of .bodyValue, since this results in chunked encoding,
            // which the endpoint may not support, resulting in 404 Not Found

            log.debug { "Token obtained for audience '$audience'" }
            OAuth2TokenDataMapper.map(body, expirationChecker.time())
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun getTokenEndpoint(): String = oauth2ConfigGetter.getTokenEndpoint()

    protected fun isExpired(token: TokenData): Boolean =
        expirationChecker.isExpired(token.issuedTime, token.expiresInSeconds)

    protected abstract fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter, audience: String
    ): MultiValueMap<String, String>

    private companion object {
        private val service = EgressService.OAUTH2_TOKEN
    }
}
