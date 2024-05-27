package no.nav.pensjon.kalkulator.omstillingsstoenad.client

import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class EtterlatteOmstillingsstoenadClientTest : WebClientTest() {

    private lateinit var client: EtterlatteOmstillingsstoenadClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = EtterlatteOmstillingsstoenadClient(
            baseUrl = baseUrl(),
            webClientBuilder = webClientBuilder,
            traceAid = traceAid,
            retryAttempts = RETRY_ATTEMPTS
        )

        arrangeSecurityContext()
    }

    @Test
    fun `bruker mottar omstillingsstoenad`() = runTest {
        arrange(okStatusResponse(true))
        assertTrue(client.mottarOmstillingsstoenad(pid, dato))
    }

    @Test
    fun `bruker mottar ikke omstillingsstoenad`() = runTest {
        arrange(okStatusResponse(false))
        assertFalse(client.mottarOmstillingsstoenad(pid, dato))
    }

    @Test
    fun `omstillinsstoenadClient gjentar request ved serverfeil`() = runTest {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okStatusResponse(true))
        assertTrue(client.mottarOmstillingsstoenad(pid, dato))
    }

    companion object{
        private const val RETRY_ATTEMPTS = "1"
        private val dato = LocalDate.now()

        private fun okStatusResponse(value: Boolean) = jsonResponse().setBody(statusResponseBody(value).trimIndent())

        @Language("json")
        private fun statusResponseBody(value: Boolean) =
            """{
                 "omstillingsstoenad": $value
             }
             """
    }
}