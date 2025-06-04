package no.nav.pensjon.kalkulator.opptjening.client.popp

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.popp.PoppOpptjeningsgrunnlagClientTestObjects.RESPONSE_BODY
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
import java.math.BigDecimal

class PoppOpptjeningsgrunnlagClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun opptjeningClient(context: BeanFactory) =
        PoppOpptjeningsgrunnlagClient(
            baseUrl!!,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            traceAid,
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

    test("fetchOpptjeningsgrunnlag returns opptjeningsgrunnlag when OK response") {
        server!!.arrangeOkJsonResponse(RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            val response: Opptjeningsgrunnlag = opptjeningClient(context = it).fetchOpptjeningsgrunnlag(pid)

            with(response.inntekter.first { it.aar == 2017 }) {
                beloep shouldBe BigDecimal("280241")
                type shouldBe Opptjeningstype.OTHER
            }
            with(response.inntekter.first { it.aar == 2018 }) {
                beloep shouldBe BigDecimal("280242")
                type shouldBe Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT
            }
        }
    }

    test("fetchOpptjeningsgrunnlag retries in case of server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeOkJsonResponse(RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            opptjeningClient(context = it).fetchOpptjeningsgrunnlag(pid)
                .inntekter[0].beloep shouldBe BigDecimal("280241")
        }
    }

    test("fetchOpptjeningsgrunnlag does not retry in case of client error") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, "My bad")
        // No 2nd response arranged, since no retry

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> {
                opptjeningClient(context = it).fetchOpptjeningsgrunnlag(pid)
            }.message shouldBe "My bad"
        }
    }

    test("fetchOpptjeningsgrunnlag handles server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil") // for retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> {
                opptjeningClient(context = it).fetchOpptjeningsgrunnlag(pid)
            }

            with(exception) {
                message shouldBe "Failed calling $baseUrl/popp/api/opptjeningsgrunnlag"
                (cause as EgressException).message shouldBe "Feil"
            }
        }
    }
})

object PoppOpptjeningsgrunnlagClientTestObjects {

    @Language("json")
    const val RESPONSE_BODY =
        """
             {
                 "opptjeningsGrunnlag": {
                     "fnr": "04925398980",
                     "inntektListe": [
                         {
                             "changeStamp": {
                                 "createdBy": "TESTDATA",
                                 "createdDate": 1586931866460,
                                 "updatedBy": "srvpensjon",
                                 "updatedDate": 1586931866775
                             },
                             "inntektId": 585473583,
                             "fnr": "04925398980",
                             "inntektAr": 2017,
                             "kilde": "PEN",
                             "kommune": "1337",
                             "piMerke": null,
                             "inntektType": "INN_LON",
                             "belop": 280241
                         },
                         {
                             "changeStamp": {
                                 "createdBy": "srvpensjon",
                                 "createdDate": 1586931866782,
                                 "updatedBy": "srvpensjon",
                                 "updatedDate": 1586931866946
                             },
                             "inntektId": 585473584,
                             "fnr": "04925398980",
                             "inntektAr": 2018,
                             "kilde": "POPP",
                             "kommune": null,
                             "piMerke": null,
                             "inntektType": "SUM_PI",
                             "belop": 280242
                         }
                     ],
                     "omsorgListe": [],
                     "dagpengerListe": [],
                     "forstegangstjeneste": null
                 }
             }
             """
}
