package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenData
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class ClientCredentialsTokenRequestClientTest : WebClientTest() {

    private lateinit var client: ClientCredentialsTokenRequestClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var expirationChecker: ExpirationChecker

    @BeforeEach
    fun initialize() {
        client = ClientCredentialsTokenRequestClient(
            tokenEndpoint = baseUrl(),
            webClientBuilder = webClientBuilder,
            expirationChecker = expirationChecker,
            credentials = ClientCredentials("id1", "secret1"),
            retryAttempts = "1"
        )
    }

    @Test
    fun `getTokenData returns token data`() {
        arrange(tokenResponse())
        `when`(expirationChecker.time()).thenReturn(LocalDateTime.MIN)

        val tokenData: TokenData = client.getTokenData(tokenAccessParameter, AUDIENCE, USER)

        assertEquals(ACCESS_TOKEN, tokenData.accessToken)
    }

    @Test
    fun `getTokenData caches token data`() {
        arrange(tokenResponse())
        `when`(expirationChecker.time()).thenReturn(LocalDateTime.MIN)

        val tokenData: TokenData = client.getTokenData(tokenAccessParameter, AUDIENCE, USER)
        // Next statement will fail if token not cached, since only one response is enqueued:
        val cachedTokenData: TokenData = client.getTokenData(tokenAccessParameter, AUDIENCE, USER)

        assertEquals(ACCESS_TOKEN, tokenData.accessToken)
        assertEquals(ACCESS_TOKEN, cachedTokenData.accessToken)
    }

    companion object {
        private const val ACCESS_TOKEN = "token1"
        private const val AUDIENCE = "audience1"
        private const val USER = "user1"
        private val tokenAccessParameter = TokenAccessParameter.clientCredentials("scope1")

        private fun tokenResponse(): MockResponse {
            // Based on actual response from TokenDings
            return jsonResponse(HttpStatus.OK)
                .setBody(
                    """
{
  "access_token": "$ACCESS_TOKEN",
  "issued_token_type": "urn:ietf:params:oauth:token-type:access_token",
  "token_type": "Bearer",
  "expires_in": 299
}"""
                )
        }
    }
}
