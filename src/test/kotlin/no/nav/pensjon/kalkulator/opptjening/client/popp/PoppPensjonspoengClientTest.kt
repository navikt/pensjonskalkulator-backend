package no.nav.pensjon.kalkulator.opptjening.client.popp

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.AarligBeholdning
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

/**
 * Bruker WireMock istedenfor MockWebServer her, siden WireMock støtter testing av parallelle kall.
 */
class PoppPensjonspoengClientTest : FunSpec({

    var server: WireMockServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)
    val cacheManager = CaffeineCacheManager()
    val opptjeningResponseBody: String = loadJson(tema = "hentPensjonspoengListe")
    val beholdningResponseBody: String = loadJson(tema = "poppBeholdningListe")

    fun pensjonspoengClient(context: BeanFactory) =
        PoppPensjonspoengClient(
            baseUrl!!,
            webClientBuilder = context.getBean<WebClient.Builder>(),
            cacheManager,
            traceAid,
            retryAttempts = "1"
        )

    beforeSpec {
        Arrange.security()
        server = WireMockServer(options().dynamicPort()).apply { start() }
        baseUrl = "http://localhost:${server.port()}"
    }

    afterSpec {
        server?.stop()
    }

    context("fetchOpptjeningOgBeholdning") {
        test("returns opptjening & beholdning when OK response") {
            configureFor("localhost", server!!.port())
            arrangeOkJsonResponse(resource = "pensjonspoeng/hent", body = opptjeningResponseBody)
            arrangeOkJsonResponse(resource = "beholdning", body = beholdningResponseBody)

            Arrange.webClientContextRunner().run {
                val response = pensjonspoengClient(context = it).fetchOpptjeningOgBeholdning(pid)

                response.first shouldBe listOf(
                    AarligOpptjening(
                        aar = 2000,
                        pensjonsgivendeInntekt = 100000,
                        pensjonspoeng = 20.0,
                        pensjonspoengType = "ORDINAR",
                        maksimalUfoeregrad = 70,
                        omsorgspoeng = 2000,
                        beholdning = 0
                    ),
                    AarligOpptjening(
                        aar = 2001,
                        pensjonsgivendeInntekt = 110000,
                        pensjonspoeng = 21.5,
                        pensjonspoengType = "OMSORG",
                        maksimalUfoeregrad = 0,
                        omsorgspoeng = null,
                        beholdning = 0
                    )
                )

                response.second shouldBe listOf(
                    AarligBeholdning(
                        aar = 1978,
                        beholdning = 12
                    ),
                    AarligBeholdning(
                        aar = 1979,
                        beholdning = 23
                    )
                )
            }
        }
    }
})

private fun arrangeOkJsonResponse(resource: String, body: String) {
    stubFor(
        post(urlEqualTo("/popp/api/$resource")).willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(body)
        )
    )
}

private fun FunSpec.loadJson(tema: String): String =
    this::class.java.getResource("/pensjonspoeng/$tema.json")?.readText(Charsets.UTF_8)!!