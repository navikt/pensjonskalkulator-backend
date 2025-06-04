package no.nav.pensjon.kalkulator.tech.security.egress.token

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClientTestObjects.ACCESS_TOKEN
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClientTestObjects.AUDIENCE
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClientTestObjects.PID1
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClientTestObjects.TOKEN_ACCESS_PARAM
import no.nav.pensjon.kalkulator.tech.security.egress.token.CacheAwareTokenClientTestObjects.tokenData
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

class CacheAwareTokenClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null

    val expirationChecker = mockk<ExpirationChecker>().apply {
        every { time() } returns LocalDateTime.of(2021, 1, 1, 1, 0, 0)
        every { isExpired(issuedTime = LocalDateTime.of(2021, 1, 1, 1, 0, 0), expiresInSeconds = 299) } returns false
    }

    fun client(context: BeanFactory) =
        TestClass(
            webClientBuilder = context.getBean(WebClient.Builder::class.java).baseUrl(baseUrl!!),
            expirationChecker
        )

    beforeTest {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterTest {
        server?.shutdown()
    }

    test("getTokenData should cache token data") {
        server?.arrangeOkJsonResponse(tokenData(1))

        Arrange.webClientContextRunner().run {
            val client = client(context = it)
            val tokenData = client.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)
            // Next statement will fail if token not cached, since only one response is enqueued:
            val cachedTokenData = client.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)

            tokenData.accessToken shouldBe "${ACCESS_TOKEN}1"
            cachedTokenData.accessToken shouldBe "${ACCESS_TOKEN}1"
        }
    }

    test("clearTokenData should remove token data from cache") {
        server?.arrangeOkJsonResponse(tokenData(1))
        server?.arrangeOkJsonResponse(tokenData(2))  // 2 responses needed since cache is cleared

        Arrange.webClientContextRunner().run {
            val client = client(context = it)
            val tokenData1 = client.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)
            tokenData1.accessToken shouldBe "${ACCESS_TOKEN}1"

            client.clearTokenData(AUDIENCE, PID1)

            val tokenData2 = client.getTokenData(TOKEN_ACCESS_PARAM, AUDIENCE, PID1)
            tokenData2.accessToken shouldBe "${ACCESS_TOKEN}2" // would be 1 if cache not cleared
        }
    }
})

private object CacheAwareTokenClientTestObjects {

    const val ACCESS_TOKEN = "token"
    const val EXPIRES_IN = 299L
    const val PID1 = "PID1"
    const val AUDIENCE = "audience1"
    val TOKEN_ACCESS_PARAM = TokenAccessParameter.clientCredentials("scope1")

    fun tokenData(number: Int): String =
        """
{
  "access_token": "$ACCESS_TOKEN$number",
  "issued_token_type": "urn:ietf:params:oauth:token-type:access_token",
  "token_type": "Bearer",
  "expires_in": $EXPIRES_IN
}"""
}

class TestClass(
    webClientBuilder: WebClient.Builder,
    expirationChecker: ExpirationChecker
) : CacheAwareTokenClient(webClientBuilder.build(), expirationChecker, "1") {

    override fun getCleanupTrigger(): Int = 1000

    override fun prepareTokenRequestBody(
        accessParameter: TokenAccessParameter,
        audience: String
    ): MultiValueMap<String, String> = LinkedMultiValueMap()
}
