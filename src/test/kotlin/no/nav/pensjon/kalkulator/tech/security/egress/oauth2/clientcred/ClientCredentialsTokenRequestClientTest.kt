package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsTokenRequestClientTestObjects.ACCESS_TOKEN
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsTokenRequestClientTestObjects.AUDIENCE
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsTokenRequestClientTestObjects.TOKEN_DATA
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsTokenRequestClientTestObjects.USER
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsTokenRequestClientTestObjects.tokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenData
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

class ClientCredentialsTokenRequestClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null

    val expirationChecker = mockk<ExpirationChecker>().apply {
        every { time() } returns LocalDateTime.of(2021, 1, 1, 1, 0, 0)
        every { isExpired(issuedTime = LocalDateTime.of(2021, 1, 1, 1, 0, 0), expiresInSeconds = 299) } returns false
    }

    fun client(context: BeanFactory) =
        ClientCredentialsTokenRequestClient(
            baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            expirationChecker,
            credentials = ClientCredentials("id1", "secret1"),
            retryAttempts = "1"
        )

    beforeTest {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterTest {
        server?.shutdown()
    }

    test("getTokenData should return token data") {
        server?.arrangeOkJsonResponse(TOKEN_DATA)

        Arrange.webClientContextRunner().run {
            client(context = it).getTokenData(tokenAccessParameter, AUDIENCE, USER).accessToken shouldBe ACCESS_TOKEN
        }
    }

    test("getTokenData should cache token data") {
        server?.arrangeOkJsonResponse(TOKEN_DATA)

        Arrange.webClientContextRunner().run {
            val client = client(context = it)
            val tokenData: TokenData = client.getTokenData(tokenAccessParameter, AUDIENCE, USER)
            // Next statement will fail if token not cached, since only one response is enqueued:
            val cachedTokenData: TokenData = client.getTokenData(tokenAccessParameter, AUDIENCE, USER)

            tokenData.accessToken shouldBe ACCESS_TOKEN
            cachedTokenData.accessToken shouldBe ACCESS_TOKEN
        }
    }
})

private object ClientCredentialsTokenRequestClientTestObjects {
    const val ACCESS_TOKEN = "token1"
    const val AUDIENCE = "audience1"
    const val USER = "user1"

    const val TOKEN_DATA = """
{
  "access_token": "$ACCESS_TOKEN",
  "issued_token_type": "urn:ietf:params:oauth:token-type:access_token",
  "token_type": "Bearer",
  "expires_in": 299
}"""

    val tokenAccessParameter = TokenAccessParameter.clientCredentials("scope1")
}
