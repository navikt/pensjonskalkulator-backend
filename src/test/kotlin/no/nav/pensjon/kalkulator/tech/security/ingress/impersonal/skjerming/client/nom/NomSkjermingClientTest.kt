package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.nom

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class NomSkjermingClientTest : WebClientTest() {

    private lateinit var client: NomSkjermingClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = NomSkjermingClient(
            baseUrl = baseUrl(),
            webClientBuilder = webClientBuilder,
            traceAid = traceAid,
            retryAttempts = RETRY_ATTEMPTS
        )

        arrangeSecurityContext()
    }

    @Test
    fun `harTilgangTilPerson returns true when person is not skjermet`() {
        arrange(skjermingResponse(erSkjermet = false))
        assertTrue(client.personErTilgjengelig(pid))
    }

    @Test
    fun `harTilgangTilPerson returns false when person is skjermet`() {
        arrange(skjermingResponse(erSkjermet = true))
        assertFalse(client.personErTilgjengelig(pid))
    }

    companion object {
        private const val RETRY_ATTEMPTS = "1"

        @Language("json")
        private fun skjermingResponseBody(erSkjermet: Boolean) = "$erSkjermet"

        private fun skjermingResponse(erSkjermet: Boolean) =
            jsonResponse().setBody(skjermingResponseBody(erSkjermet).trimIndent())
    }
}
