package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.behandlet.*
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.web.reactive.function.client.WebClient

class BehandletOpptjeningClientTest : ShouldSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid: TraceAid = mockk { every { callId() } returns "id1" }

    fun client(context: BeanFactory, retryAttempts: String = "1") =
        BehandletOpptjeningClient(
            baseUrl!!,
            retryAttempts,
            webClientBuilder = context.getBean<WebClient.Builder>(),
            cacheManager = mockk(relaxed = true),
            pidEncrypter = mockk { every { encrypt(any()) } returns "x.y" },
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
            server?.arrangeOkJsonResponse(OK_RESPONSE_BODY)

            Arrange.webClientContextRunner().run {
                client(context = it).fetchBeholdningOgMerknader(pid) shouldBe
                        AarvisBeholdningOgMerknader(
                            perAar = mapOf(
                                2023 to BeholdningOgMerknader(
                                    beholdning = Beholdning(beloep = 230000),
                                    merknadListe = listOf(Merknad(kode = MerknadCode.AFP))
                                ),
                                2024 to BeholdningOgMerknader(
                                    beholdning = Beholdning(beloep = 240000),
                                    merknadListe = emptyList()
                                ),
                                2025 to BeholdningOgMerknader(
                                    beholdning = Beholdning(beloep = 250000),
                                    merknadListe = listOf(
                                        Merknad(
                                            kode = MerknadCode.UTTAK_STOPPET,
                                            uttaksgrad = UttaksgradMerknad(
                                                grad = Uttaksgrad.NULL,
                                                kalendermaanedFom = 4
                                            )
                                        ),
                                        Merknad(
                                            kode = MerknadCode.GRADERT_UTTAK,
                                            uttaksgrad = UttaksgradMerknad(
                                                grad = Uttaksgrad.TJUE_PROSENT,
                                                kalendermaanedFom = 2
                                            )
                                        )
                                    )
                                ),
                                2026 to BeholdningOgMerknader(
                                    beholdning = Beholdning(beloep = 260000),
                                    merknadListe = listOf(
                                        Merknad(
                                            kode = MerknadCode.HELT_UTTAK,
                                            uttaksgrad = UttaksgradMerknad(
                                                grad = Uttaksgrad.HUNDRE_PROSENT,
                                                kalendermaanedFom = 6
                                            )
                                        )
                                    )
                                )
                            )
                        )
            }
        }
    }
})

@Language("json")
const val OK_RESPONSE_BODY = """{
    "perAar": {
        "2023": {
            "beholdning": {
                "beloep": 230000
            },
            "merknadListe": [
                {
                    "kode": "AFP"
                }
            ]
        },
        "2024": {
            "beholdning": {
                "beloep": 240000
            },
            "merknadListe": []
        },
        "2025": {
            "beholdning": {
                "beloep": 250000
            },
            "merknadListe": [
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
            ]
        },
        "2026": {
            "beholdning": {
                "beloep": 260000
            },
            "merknadListe": [
                {
                    "kode": "HELT_UTTAK",
                    "uttaksgrad": {
                        "grad": 100,
                        "kalendermaanedFom": 6
                    }
                }
            ]
        }
    }
}"""