package no.nav.pensjon.kalkulator.lagring.client.skribenten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.lagring.LagreAlderspensjon
import no.nav.pensjon.kalkulator.lagring.LagreSimulering
import no.nav.pensjon.kalkulator.lagring.LagreVilkaarsproevingsresultat
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

class SkribentenClientTest : ShouldSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    fun client(context: BeanFactory) =
        SkribentenClient(
            baseUrl = baseUrl!!,
            webClientBuilder = context.getBean<WebClient.Builder>(),
            traceAid = traceAid,
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

    should("returnere brev-respons ved vellykket lagring") {
        server?.arrangeOkJsonResponse(BREV_RESPONSE)

        Arrange.webClientContextRunner().run {
            val response = client(it).lagreSimulering(SAK_ID, simulering())

            response.brevId shouldBe "brev-123"
            response.sakId shouldBe "sak-456"
        }
    }

    should("sende request til korrekt URI med sak-ID") {
        server?.arrangeOkJsonResponse(BREV_RESPONSE)

        Arrange.webClientContextRunner().run {
            client(it).lagreSimulering(SAK_ID, simulering())

            server?.takeRequest()?.path shouldBe "/sak/$SAK_ID/brev"
        }
    }

    should("kaste EgressException ved serverfeil") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR)
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR) // retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> {
                client(it).lagreSimulering(SAK_ID, simulering())
            }

            exception.isClientError shouldBe false
        }
    }

    should("kaste EgressException med isClientError ved 4xx-feil") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, CLIENT_ERROR)
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, CLIENT_ERROR) // retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> {
                client(it).lagreSimulering(SAK_ID, simulering())
            }

            exception.isClientError shouldBe true
        }
    }
}) {
    companion object {
        private const val SAK_ID = 42L

        @Language("json")
        private val BREV_RESPONSE = """{
            "info": {
                "id": "brev-123",
                "saksId": "sak-456"
            }
        }"""

        @Language("json")
        private val SERVER_ERROR = """{
            "status": 500,
            "error": "Internal Server Error"
        }"""

        @Language("json")
        private val CLIENT_ERROR = """{
            "status": 400,
            "error": "Bad Request"
        }"""

        private fun simulering() = LagreSimulering(
            alderspensjonListe = listOf(LagreAlderspensjon(alderAar = 67, beloep = 250000, gjenlevendetillegg = null)),
            livsvarigOffentligAfpListe = emptyList(),
            tidsbegrensetOffentligAfp = null,
            privatAfpListe = emptyList(),
            vilkaarsproevingsresultat = LagreVilkaarsproevingsresultat(erInnvilget = true, alternativ = null),
            trygdetid = null,
            pensjonsgivendeInntektListe = emptyList(),
            enhetsId = "4817"
        )
    }
}
