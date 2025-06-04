package no.nav.pensjon.kalkulator.ufoere.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import no.nav.pensjon.kalkulator.ufoere.client.pen.PenUfoeregradClientTestObjecs.RESPONSE_WITHOUT_GRAD
import no.nav.pensjon.kalkulator.ufoere.client.pen.PenUfoeregradClientTestObjecs.RESPONSE_WITH_ACTUAL_GRAD
import no.nav.pensjon.kalkulator.ufoere.client.pen.PenUfoeregradClientTestObjecs.UFOEREGRAD
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

class PenUfoeregradClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }

    fun client(context: BeanFactory) =
        PenUfoeregradClient(
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

    test("hent uføregrad for person med uføretrygd") {
        server?.arrangeOkJsonResponse(RESPONSE_WITH_ACTUAL_GRAD)
        Arrange.webClientContextRunner().run {
            client(context = it).hentUfoeregrad(pid).uforegrad shouldBe UFOEREGRAD
        }
    }

    test("hent uføregrad for person uten uføretrygd") {
        server?.arrangeOkJsonResponse(RESPONSE_WITHOUT_GRAD)
        Arrange.webClientContextRunner().run {
            client(context = it).hentUfoeregrad(pid).uforegrad shouldBe 0
        }
    }

    test("hent uføregrad retries in case of server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeOkJsonResponse(RESPONSE_WITH_ACTUAL_GRAD)

        Arrange.webClientContextRunner().run {
            client(context = it).hentUfoeregrad(pid).uforegrad shouldBe UFOEREGRAD
        }
    }

    test("hent uføregrad does not retry in case of client error") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, "My bad")
        // No 2nd response arranged, since no retry

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> { client(context = it).hentUfoeregrad(pid) }.message shouldBe "My bad"
        }
    }

    test("hent uføregrad handles server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil") // for retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> { client(context = it).hentUfoeregrad(pid) }

            with(exception) {
                message shouldBe "Failed calling /api/uforetrygd/uforegrad/seneste"
                (cause as EgressException).message shouldBe "Feil"
            }
        }
    }
})

object PenUfoeregradClientTestObjecs {
    const val UFOEREGRAD = 80

    // fun okResponse() = jsonResponse(HttpStatus.OK).setBody(RESPONSE_WITH_ACTUAL_GRAD)
    //fun okResponseWithoutUfoeregrad() = jsonResponse(HttpStatus.OK).setBody(RESPONSE_WITHOUT_GRAD)

    @Language("JSON")
    const val RESPONSE_WITH_ACTUAL_GRAD = """
{
    "uforegrad": $UFOEREGRAD
}
"""

    @Language("JSON")
    const val RESPONSE_WITHOUT_GRAD = """
{
    "uforegrad": null
}
"""
}
