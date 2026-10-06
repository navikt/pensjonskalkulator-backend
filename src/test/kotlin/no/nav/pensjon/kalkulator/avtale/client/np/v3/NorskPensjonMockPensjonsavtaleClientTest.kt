package no.nav.pensjon.kalkulator.avtale.client.np.v3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.avtaleSpec
import no.nav.pensjon.kalkulator.mock.Saml
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import okhttp3.mockwebserver.MockWebServer
import org.springframework.web.reactive.function.client.WebClient

class NorskPensjonMockPensjonsavtaleClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("fetchAvtaler returns no agreements without calling the mock service") {
        Arrange.webClientContextRunner().run {
            val result = NorskPensjonMockPensjonsavtaleClient(
                baseUrl!!,
                tokenGetter = mockk<SamlTokenService>().apply { every { assertion() } returns Saml.ASSERTION },
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" },
                xmlMapper = xmlMapper(),
                retryAttempts = "1"
            ).fetchAvtaler(avtaleSpec)

            result shouldBe Pensjonsavtaler(emptyList(), emptyList())
            server?.requestCount shouldBe 0
        }
    }
})

