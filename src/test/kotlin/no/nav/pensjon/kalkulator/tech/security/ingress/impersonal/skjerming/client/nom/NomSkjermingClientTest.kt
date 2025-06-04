package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.nom

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.BeanFactory
import org.springframework.web.reactive.function.client.WebClient

class NomSkjermingClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        NomSkjermingClient(
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

    test("harTilgangTilPerson should return 'true' when person is not skjermet") {
        server?.arrangeOkJsonResponse("false") // false => ikke skjermet

        Arrange.webClientContextRunner().run {
            client(context = it).personErTilgjengelig(pid) shouldBe true
        }
    }

    test("harTilgangTilPerson should return 'false' when person is skjermet") {
        server?.arrangeOkJsonResponse("true") // true => skjermet

        Arrange.webClientContextRunner().run {
            client(context = it).personErTilgjengelig(pid) shouldBe false
        }
    }
})
