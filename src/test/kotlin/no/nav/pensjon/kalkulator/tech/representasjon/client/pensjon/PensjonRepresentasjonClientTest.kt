package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.TestObjects.jwt
import no.nav.pensjon.kalkulator.mock.TestObjects.pid1
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.WebClient

class PensjonRepresentasjonClientTest : FunSpec({
    var server: MockWebServer? = null
    var baseUrl: String? = null

    beforeSpec {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

        SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
            initialAuth = TestingAuthenticationToken("TEST_USER", jwt),
            egressTokenSuppliersByService = EgressTokenSuppliersByService(mapOf()),
            target = RepresentasjonTarget(pid = pid1, rolle = RepresentertRolle.FULLMAKT_GIVER)
        )

        server = MockWebServer().also { it.start() }
        baseUrl = server.let { "http://localhost:${it.port}" }
    }

    afterSpec {
        server?.shutdown()
    }

    test("hasValidRepresentasjonsforhold") {
        val contextRunner = ApplicationContextRunner().withConfiguration(
            AutoConfigurations.of(WebClientAutoConfiguration::class.java)
        )

        server?.enqueue(
            MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("""{ "hasValidRepresentasjonsforhold": true, "fullmaktsgiverNavn": "Abc Æøå"}""")
        )

        contextRunner.run {
            val client = PensjonRepresentasjonClient(
                baseUrl = baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid = mock(TraceAid::class.java),
                retryAttempts = "0"
            )

            val result: Representasjon = client.hasValidRepresentasjonsforhold(pid)

            result shouldBe Representasjon(
                isValid = true,
                fullmaktGiverNavn = "Abc Æøå"
            )

            server?.takeRequest()?.requestUrl?.query shouldBe "validRepresentasjonstyper=PENSJON_FULLSTENDIG" +
                    "&validRepresentasjonstyper=PENSJON_SKRIV" +
                    "&validRepresentasjonstyper=PENSJON_PENGEMOTTAKER" +
                    "&validRepresentasjonstyper=PENSJON_VERGE" +
                    "&validRepresentasjonstyper=PENSJON_VERGE_PENGEMOTTAKER" +
                    "&includeFullmaktsgiverNavn=false"
        }
    }
})
