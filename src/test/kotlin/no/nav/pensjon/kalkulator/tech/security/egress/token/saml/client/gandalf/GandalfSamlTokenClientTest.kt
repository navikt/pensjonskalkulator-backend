package no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.GandalfSamlTokenClientTestObjects.SAML_TOKEN_RESPONSE_BODY
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.web.reactive.function.client.WebClient

class GandalfSamlTokenClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        GandalfSamlTokenClient(
            baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            traceAid,
            retryAttempts = "1"
        )

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("fetchSamlToken returns access token data when OK response") {
        server?.arrangeOkJsonResponse(SAML_TOKEN_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            val response = client(context = it).fetchSamlToken()

            with(response) {
                access_token shouldBe "PHNhb...lvbj4"
                issued_token_type shouldBe "urn:ietf:params:oauth:token-type:saml2"
                token_type shouldBe "Bearer"
                expires_in shouldBe 2649
            }
        }
    }
})

private object GandalfSamlTokenClientTestObjects {

    // access_token is a Base64-encoded SAML token
    @Language("json")
    const val SAML_TOKEN_RESPONSE_BODY = """{
    "access_token": "PHNhb...lvbj4",
    "issued_token_type": "urn:ietf:params:oauth:token-type:saml2",
    "token_type": "Bearer",
    "expires_in": 2649
}"""
}
