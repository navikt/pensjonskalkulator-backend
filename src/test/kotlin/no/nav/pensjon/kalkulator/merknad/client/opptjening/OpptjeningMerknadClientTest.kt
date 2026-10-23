package no.nav.pensjon.kalkulator.merknad.client.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.merknad.Merknad
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.Merknader
import no.nav.pensjon.kalkulator.merknad.UttaksgradMerknad
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.web.reactive.function.client.WebClient

class OpptjeningMerknadClientTest : ShouldSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid: TraceAid = mockk { every { callId() } returns "id1" }

    fun client(context: BeanFactory, retryAttempts: String = "1") =
        OpptjeningMerknadClient(
            baseUrl!!,
            retryAttempts,
            webClientBuilder = context.getBean<WebClient.Builder>(),
            mockk(relaxed = true),
            mockk { every { encrypt(any()) } returns "x.y" },
            traceAid
        )

    beforeTest {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterTest {
        server?.shutdown()
    }

    context("respons-JSON har merknader") {
        should("inkludere merknader i responsobjektet") {
            server?.arrangeOkJsonResponse(MERKNADER_RESPONSE_BODY)

            Arrange.webClientContextRunner().run {

                val response: Merknader =
                    client(context = it).fetchMerknader(pid)

                response shouldBe Merknader(
                    perAar = mapOf(
                        2023 to listOf(Merknad(kode = MerknadCode.INGEN_OPPTJENING)),
                        2024 to emptyList(),
                        2025 to listOf(
                            Merknad(
                                kode = MerknadCode.UTTAK_STOPPET,
                                uttaksgrad = UttaksgradMerknad(grad = 0, kalendermaanedFom = 4)
                            ),
                            Merknad(
                                kode = MerknadCode.GRADERT_UTTAK,
                                uttaksgrad = UttaksgradMerknad(grad = 20, kalendermaanedFom = 2)
                            )
                        ),
                        2026 to listOf(
                            Merknad(
                                kode = MerknadCode.HELT_UTTAK,
                                uttaksgrad = UttaksgradMerknad(grad = 100, kalendermaanedFom = 6)
                            )
                        )
                    )
                )
            }
        }
    }
})

@Language("json")
const val MERKNADER_RESPONSE_BODY = """{
    "merknaderPerAar": {
        "2023": [
            {
                "kode": "INGEN_OPPTJENING"
            }
        ],
        "2024": [],
        "2025": [
            {
                "kode": "UTTAK_STOPPET",
                "uttaksgrad": {
                    "grad": 0,
                    "kalendermaanedFom": 4
                }
            },
            {
                "kode": "GRADERT_UTTAK",
                "uttaksgrad": {
                    "grad": 20,
                    "kalendermaanedFom": 2
                }
            }
        ],
        "2026": [
            {
                "kode": "HELT_UTTAK",
                "uttaksgrad": {
                    "grad": 100,
                    "kalendermaanedFom": 6
                }
            }
        ]
    }
}"""

