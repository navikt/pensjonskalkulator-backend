package no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.WebClientTest
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class GandalfSamlTokenClientTest : WebClientTest() {

    private lateinit var client: GandalfSamlTokenClient

    @BeforeEach
    fun initialize() {
        client = GandalfSamlTokenClient(baseUrl(), WebClient.create())
    }

    @Test
    fun `fetchSamlToken returns access token data when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response = client.fetchSamlToken()

        assertEquals("PHNhb...lvbj4", response.access_token)
        assertEquals("urn:ietf:params:oauth:token-type:saml2", response.issued_token_type)
        assertEquals("Bearer", response.token_type)
        assertEquals(2649, response.expires_in)
    }

    private companion object {

        // access_token is a Base64-encoded SAML token
        @Language("json")
        private const val SAML_TOKEN_RESPONSE_BODY = """{
    "access_token": "PHNhb...lvbj4",
    "issued_token_type": "urn:ietf:params:oauth:token-type:saml2",
    "token_type": "Bearer",
    "expires_in": 2649
}"""

        private fun okResponse() = jsonResponse().setBody(SAML_TOKEN_RESPONSE_BODY)
    }
}
