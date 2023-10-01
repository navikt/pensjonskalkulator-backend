package no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class FssGatewayUsernameTokenClientTest : WebClientTest() {

    private lateinit var client: FssGatewayUsernameTokenClient

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = FssGatewayUsernameTokenClient(
            baseUrl = baseUrl(),
            webClient = WebClientConfig().regularWebClient(),
            traceAid = traceAid,
            retryAttempts = "1"
        )
    }

    @Test
    fun `fetchUsernameToken returns access token data when OK response`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response = client.fetchUsernameToken()

        assertEquals(WS_SECURITY_ELEMENT, response.token)
    }

    private companion object {

        @Language("xml")
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
