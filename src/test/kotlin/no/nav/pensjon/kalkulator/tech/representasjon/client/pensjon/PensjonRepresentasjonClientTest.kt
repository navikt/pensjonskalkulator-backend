package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.PossiblyEncryptedPid
import no.nav.pensjon.kalkulator.tech.representasjon.Personalia
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonSpec
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
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
            server!!.arrangeOkJsonResponse(RESPONSE_BODY)

            Arrange.webClientContextRunner().run {
                val client = PensjonRepresentasjonClient(
                    baseUrl = baseUrl!!,
                    webClientBuilder = it.getBean<WebClient.Builder>(),
                    cacheManager = mockk(relaxed = true),
                    traceAid = mockk(relaxed = true),
                    retryAttempts = "0"
                )

                client.fetchRepresentasjon(
                    spec = RepresentasjonSpec(
                        fullmaktsgiverPid = PossiblyEncryptedPid("kryptert.verdi"),
                        fullmektigPid = pid,
                        gyldigeRepresentasjonstyper = emptyList(),
                        inkluderRepresentertNavn = false
                    )
                ) shouldBe Representasjon(
                    isValid = true,
                    fullmaktsgiver = Personalia(navn = "Abc Æøå", pid)
                )

                server.takeRequest().body.readUtf8() shouldBe
                        """{"representertPid":"kryptert.verdi","representantPid":"12906498357","validRepresentasjonstyper":[],"includeRepresentertNavn":false}"""
            }
        }
    }
})

@Language("JSON")
private const val RESPONSE_BODY =
    """{"hasValidRepresentasjonsforhold":true,"representertNavn":"Abc Æøå","representertPidKryptert":"bd3f6f90.cqwvjk4El6mr1k935Ta39hpq2jkrSgzGydO684tpR1RhthhDtuMjle38KFRhJpbO2dYW1y5pF7-IzopIl7fcJQ","representertPid":"12906498357"}
"""