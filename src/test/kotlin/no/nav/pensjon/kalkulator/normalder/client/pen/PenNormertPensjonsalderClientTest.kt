package no.nav.pensjon.kalkulator.normalder.client.pen

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.TestObjects.jwt
import no.nav.pensjon.kalkulator.mock.TestObjects.pid1
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.reactive.function.client.WebClient

class PenNormertPensjonsalderClientTest : FunSpec({
    var server: MockWebServer? = null
    var baseUrl: String? = null

    beforeSpec {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

        SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
            initialAuth = TestingAuthenticationToken("TEST_USER", jwt),
            egressTokenSuppliersByService = EgressTokenSuppliersByService(mapOf()),
            target = RepresentasjonTarget(pid = pid1, rolle = RepresentertRolle.SELV)
        )

        server = MockWebServer().also { it.start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("fetchNormalderListe should return liste med normerte pensjonsaldre") {
        val contextRunner = ApplicationContextRunner().withConfiguration(
            AutoConfigurations.of(WebClientAutoConfiguration::class.java)
        )

        server?.enqueue(
            MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value()).setBody(PenNormalderResponse.BODY)
        )

        contextRunner.run {
            val client = PenNormertPensjonsalderClient(
                baseUrl!!,
                retryAttempts = "0",
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                cacheManager = CaffeineCacheManager(),
                traceAid = mockk(relaxed = true)
            )

            client.fetchNormalderListe() shouldBe listOf(
                Aldersgrenser(
                    aarskull = 1900,
                    nedreAlder = Alder(62, 0),
                    normalder = Alder(67, 0),
                    oevreAlder = Alder(75, 0),
                    verdiStatus = VerdiStatus.FAST
                ),
                Aldersgrenser(
                    aarskull = 2025,
                    nedreAlder = Alder(62, 0),
                    normalder = Alder(67, 0),
                    oevreAlder = Alder(75, 0),
                    verdiStatus = VerdiStatus.FAST
                ),
                Aldersgrenser(
                    aarskull = 2026,
                    nedreAlder = Alder(62, 0),
                    normalder = Alder(67, 0),
                    oevreAlder = Alder(75, 0),
                    verdiStatus = VerdiStatus.PROGNOSE
                ),
                Aldersgrenser(
                    aarskull = 2027,
                    nedreAlder = Alder(62, 1),
                    normalder = Alder(67, 1),
                    oevreAlder = Alder(75, 1),
                    verdiStatus = VerdiStatus.PROGNOSE
                ),
                Aldersgrenser(
                    aarskull = 2033,
                    nedreAlder = Alder(63, 0),
                    normalder = Alder(68, 0),
                    oevreAlder = Alder(76, 0),
                    verdiStatus = VerdiStatus.PROGNOSE
                )
            )
        }
    }
})

object PenNormalderResponse {

    @Language("json")
    const val BODY = """{
    "normertPensjonsalderListe": [
        {
            "aarskull": 1900,
            "aar": 67,
            "maaned": 0,
            "nedreAar": 62,
            "nedreMaaned": 0,
            "oevreAar": 75,
            "oevreMaaned": 0,
            "type": "FAST"
        },
        {
            "aarskull": 2025,
            "aar": 67,
            "maaned": 0,
            "nedreAar": 62,
            "nedreMaaned": 0,
            "oevreAar": 75,
            "oevreMaaned": 0,
            "type": "FAST"
        },
        {
            "aarskull": 2026,
            "aar": 67,
            "maaned": 0,
            "nedreAar": 62,
            "nedreMaaned": 0,
            "oevreAar": 75,
            "oevreMaaned": 0,
            "type": "PROGNOSE"
        },
        {
            "aarskull": 2027,
            "aar": 67,
            "maaned": 1,
            "nedreAar": 62,
            "nedreMaaned": 1,
            "oevreAar": 75,
            "oevreMaaned": 1,
            "type": "PROGNOSE"
        },
        {
            "aarskull": 2033,
            "aar": 68,
            "maaned": 0,
            "nedreAar": 63,
            "nedreMaaned": 0,
            "oevreAar": 76,
            "oevreMaaned": 0,
            "type": "PROGNOSE"
        }
    ]
}"""
}
