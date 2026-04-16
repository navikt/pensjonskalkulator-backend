package no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnheter
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnhet
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper

class NavAnsattClientTest : ShouldSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    fun client(context: BeanFactory, jsonMapper: JsonMapper) =
        NavAnsattClient(
            baseUrl!!,
            retryAttempts = "1",
            webClientBuilder = context.getBean<WebClient.Builder>(),
            cacheManager = CaffeineCacheManager(),
            jsonMapper,
            traceAid
        )

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    should("return enhetsliste when service returns 200 OK") {
        server?.arrangeOkJsonResponse(
            body = """[
                {
                    "id": "4833",
                    "navn": "Nav familie- og pensjonsytelser Oslo 1",
                    "nivaa": "EN"
                },
                {
                    "id": "4475",
                    "navn": "Nav arbeid og ytelser - uføretrygd bosatt utland",
                    "nivaa": "SPESEN"
                },
                {
                    "id": "4415",
                    "navn": "Nav arbeid og ytelser Møre og Romsdal",
                    "nivaa": "EN"
                }
            ]"""
        )

        Arrange.webClientContextRunner().run {
            client(context = it, jsonMapper).fetchTjenestekontorEnhetListe(ansattId = ANSATT_ID) shouldBe
                    TjenestekontorEnheter(
                        listOf(
                            TjenestekontorEnhet(
                                id = "4833",
                                navn = "Nav familie- og pensjonsytelser Oslo 1",
                                nivaa = "EN"
                            ),
                            TjenestekontorEnhet(
                                id = "4475",
                                navn = "Nav arbeid og ytelser - uføretrygd bosatt utland",
                                nivaa = "SPESEN"
                            ),
                            TjenestekontorEnhet(
                                id = "4415",
                                navn = "Nav arbeid og ytelser Møre og Romsdal",
                                nivaa = "EN"
                            )
                        ),
                        problem = null
                    )
        }
    }

    should("return problem with details when service returns 404 Not Found") {
        server?.arrangeJsonResponse(
            status = HttpStatus.NOT_FOUND,
            body = """{"message":"User with ID $ANSATT_ID not found"}"""
        )

        Arrange.webClientContextRunner().run {
            client(
                context = it,
                jsonMapper
            ).fetchTjenestekontorEnhetListe(ansattId = ANSATT_ID) shouldBe
                    TjenestekontorEnheter(
                        enhetListe = emptyList(),
                        problem = Problem(
                            type = ProblemType.PERSON_IKKE_FUNNET,
                            beskrivelse = "User with ID $ANSATT_ID not found"
                        )
                    )
        }
    }
})

private const val ANSATT_ID = "X123456"

private val jsonMapper: JsonMapper =
    JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()
