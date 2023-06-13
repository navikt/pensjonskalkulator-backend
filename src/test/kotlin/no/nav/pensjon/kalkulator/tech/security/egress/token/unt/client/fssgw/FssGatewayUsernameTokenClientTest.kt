package no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.WebClientTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class FssGatewayUsernameTokenClientTest : WebClientTest() {

    private lateinit var client: FssGatewayUsernameTokenClient

    @BeforeEach
    fun initialize() {
        client = FssGatewayUsernameTokenClient(baseUrl(), WebClient.create())
    }

    @Test
    fun `fetchUsernameToken returns access token data when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response = client.fetchUsernameToken()

        assertEquals(WS_SECURITY_ELEMENT, response.token)
    }

    private companion object {

        private const val WS_SECURITY_ELEMENT =
            """<wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
                <wsse:UsernameToken>
                    <wsse:Username>srvpselv</wsse:Username>
                    <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">&amp;secret</wsse:Password>
                </wsse:UsernameToken>
            </wsse:Security>"""

        private fun okResponse() = jsonResponse().setBody(WS_SECURITY_ELEMENT)
    }
}
