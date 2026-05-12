package no.nav.pensjon.kalkulator.lagring.client.skribenten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
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
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

class SkribentenClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    fun client(context: BeanFactory) =
        SkribentenClient(
            baseUrl = baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            traceAid = traceAid,
            retryAttempts = "1"
        )

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server!!.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("lagreSimulering returnerer brev-respons ved vellykket lagring") {
        server?.arrangeOkJsonResponse(BREV_RESPONSE)

        Arrange.webClientContextRunner().run {
            val response = client(it).lagreSimulering(SAK_ID, simulering())

            response.brevId shouldBe BREV_ID
            response.sakId shouldBe SAK_ID_STRING
        }
    }

    test("lagreSimulering sender request til korrekt URI med sakId") {
        server?.arrangeOkJsonResponse(BREV_RESPONSE)

        Arrange.webClientContextRunner().run {
            client(it).lagreSimulering(SAK_ID, simulering())

            server?.takeRequest()?.path shouldBe "/sak/$SAK_ID/brev"
        }
    }

    test("lagreSimulering kaster EgressException ved server-feil") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR)
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR) // retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> {
                client(it).lagreSimulering(SAK_ID, simulering())
            }

            exception.isClientError shouldBe false
        }
    }

    test("lagreSimulering kaster EgressException med isClientError ved 4xx-feil") {
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
        private const val SAK_ID_STRING = "sak-456"
        private const val BREV_ID = "brev-123"

        @Language("json")
        private val BREV_RESPONSE = """{
            "info": {
                "id": "$BREV_ID",
                "saksId": "$SAK_ID_STRING"
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
            simuleringsinformasjon = null,
            enhetsId = "4817"
        )
    }
}
