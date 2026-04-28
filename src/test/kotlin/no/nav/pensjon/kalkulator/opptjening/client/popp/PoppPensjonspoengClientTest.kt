package no.nav.pensjon.kalkulator.opptjening.client.popp

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Pensjonspoeng
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.http.HttpStatus

class PoppPensjonspoengClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)
    val responseBody = this::class.java.getResource("/pensjonspoeng/hentPensjonspoengListe.json")?.readText(Charsets.UTF_8)!!

    fun pensjonspoengClient(context: BeanFactory) =
        PoppPensjonspoengClient(
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

    test("fetchPensjonspoeng returns pensjonspoeng when OK response") {
        server?.arrangeOkJsonResponse(responseBody)

        Arrange.webClientContextRunner().run {
            val response: List<Pensjonspoeng> = pensjonspoengClient(context = it).fetchPensjonspoeng(pid)

            response shouldBe listOf(
                Pensjonspoeng(
                    ar = 2000,
                    pensjonsgivendeInntekt = 100000,
                    pensjonspoeng = 20.0,
                    pensjonspoengType = "ORDINAR",
                    maksUforegrad = 70,
                    omsorgspoeng = 2000
                ),
                Pensjonspoeng(
                    ar = 2001,
                    pensjonsgivendeInntekt = 110000,
                    pensjonspoeng = 21.5,
                    pensjonspoengType = "OMSORG",
                    maksUforegrad = 0,
                    omsorgspoeng = null
                )
            )
        }
    }

    test("fetchPensjonspoeng retries in case of server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeOkJsonResponse(responseBody)

        Arrange.webClientContextRunner().run {
            pensjonspoengClient(context = it).fetchPensjonspoeng(pid).first().pensjonsgivendeInntekt shouldBe 100000
        }
    }

    test("fetchPensjonspoeng does not retry in case of client error") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, "My bad")

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> {
                pensjonspoengClient(context = it).fetchPensjonspoeng(pid)
            }.message shouldBe "My bad"
        }
    }

    test("fetchPensjonspoeng handles server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> {
                pensjonspoengClient(context = it).fetchPensjonspoeng(pid)
            }

            with(exception) {
                message shouldBe "Failed calling $baseUrl/popp/api/pensjonspoeng/hent"
                (cause as EgressException).message shouldBe "Feil"
            }
        }
    }
})
