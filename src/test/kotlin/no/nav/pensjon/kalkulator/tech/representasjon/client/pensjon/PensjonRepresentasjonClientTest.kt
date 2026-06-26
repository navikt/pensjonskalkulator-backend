package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.person.EncryptedPid
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.getBean
import org.springframework.web.reactive.function.client.WebClient

class PensjonRepresentasjonClientTest : ShouldSpec({

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

    context("valid representasjon") {
        should("returnere fullmaktsgiver") {
            server!!.arrangeOkJsonResponse("""{ "hasValidRepresentasjonsforhold": true, "fullmaktsgiverNavn": "Abc Æøå"}""")

            Arrange.webClientContextRunner().run {
                val client = PensjonRepresentasjonClient(
                    baseUrl = baseUrl!!,
                    webClientBuilder = it.getBean<WebClient.Builder>(),
                    traceAid = mockk<TraceAid>(relaxed = true),
                    retryAttempts = "0"
                )

                client.hasValidRepresentasjonsforhold(fullmaktsgiverPid = EncryptedPid("kryptert.verdi")) shouldBe
                        Representasjon(isValid = true, fullmaktGiverNavn = "Abc Æøå")

                server.takeRequest().requestUrl?.query shouldBe
                        "validRepresentasjonstyper=PENSJON_LES" +
                        "&validRepresentasjonstyper=PENSJON_SKRIV" +
                        "&validRepresentasjonstyper=VERGE_PENSJON_LES" +
                        "&validRepresentasjonstyper=VERGE_PENSJON_SKRIV" +
                        "&includeFullmaktsgiverNavn=false"
            }
        }
    }
})