package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config.OAuth2ConfigurationGetter
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class CacheAwareTokenClientTest : WebClientTest() {

    private lateinit var tokenGetter: TestClass

    @Mock
    private lateinit var oauth2ConfigGetter: OAuth2ConfigurationGetter

    @Mock
    private lateinit var expirationChecker: ExpirationChecker

    @BeforeEach
    fun initialize() {
        tokenGetter = TestClass(WebClient.create(), oauth2ConfigGetter, expirationChecker)
    }

    @Test
    fun `getTokenData caches token data()`() {
        arrange(tokenResponse(1))
        `when`(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl())
        `when`(expirationChecker.time()).thenReturn(LocalDateTime.MIN)

        val tokenData = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)
        // Next statement will fail if token not cached, since only one response is enqueued:
        val cachedTokenData = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)

        assertEquals("${ACCESS_TOKEN}1", tokenData.accessToken)
        assertEquals("${ACCESS_TOKEN}1", cachedTokenData.accessToken)
    }

    @Test
    fun `clearTokenData removes token data from cache`() {
        arrange(tokenResponse(1))
        arrange(tokenResponse(2)) // 2 responses needed since cache is cleared
        `when`(oauth2ConfigGetter.getTokenEndpoint()).thenReturn(baseUrl())
        `when`(expirationChecker.time()).thenReturn(LocalDateTime.MIN)
        val tokenData1 = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)
        assertEquals("${ACCESS_TOKEN}1", tokenData1.accessToken)

        tokenGetter.clearTokenData(AUDIENCE, PID1)

        val tokenData2 = tokenGetter.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)
        assertEquals("${ACCESS_TOKEN}2", tokenData2.accessToken) // would be 1 if cache not cleared
    }

    private fun tokenResponse(number: Int): MockResponse {
        return jsonResponse(HttpStatus.OK)
            .setBody(
                """
{
  "access_token": "$ACCESS_TOKEN$number",
  "issued_token_type": "urn:ietf:params:oauth:token-type:access_token",
  "token_type": "Bearer",
  "expires_in": $EXPIRES_IN
}"""
            )
    }

    private class TestClass(
        webClient: WebClient,
        oauth2ConfigGetter: OAuth2ConfigurationGetter,
        expirationChecker: ExpirationChecker
    ) : CacheAwareTokenClient(webClient, oauth2ConfigGetter, expirationChecker, "1") {
        private var cleanupTrigger = 1000

        override fun getCleanupTrigger(): Int {
            return cleanupTrigger
        }

        override fun prepareTokenRequestBody(
            accessParameter: TokenAccessParameter,
            audience: String
        ): MultiValueMap<String, String> {
            return LinkedMultiValueMap()
        }
    }

    companion object {
        private const val ACCESS_TOKEN = "token"
        private const val EXPIRES_IN = 299L
        private const val PID1 = "PID1"
        private const val AUDIENCE = "audience1"
        private val TOKEN_ACCESS_PARAM = TokenAccessParameter.clientCredentials("scope1")
    }
}
