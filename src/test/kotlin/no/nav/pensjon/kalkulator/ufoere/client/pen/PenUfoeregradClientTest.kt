package no.nav.pensjon.kalkulator.ufoere.client.pen

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PenUfoeregradClientTest : WebClientTest() {

    private lateinit var client: PenUfoeregradClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        `when`(traceAid.callId()).thenReturn("id1")
        client = PenUfoeregradClient(baseUrl(), webClientBuilder, traceAid, "1")
        arrangeSecurityContext()
    }

    @Test
    fun hentUfoeregrad() {
        arrange(okResponse())
        client.hentUfoeregrad(pid).uforegrad shouldBe UFOEREGRAD
    }

    @Test
    fun `hent ufoeregrad for brukere uten ufoeretrygd`() {
        arrange(okResponseWithoutUfoeregrad())
        client.hentUfoeregrad(pid).uforegrad shouldBe INGEN_UFOEREGRAD
    }

    @Test
    fun `hent ufoeregrad retries in case of server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(okResponse())

        client.hentUfoeregrad(pid).uforegrad shouldBe UFOEREGRAD
    }

    @Test
    fun `hent ufoeregrad does not retry in case of client error`() {
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        shouldThrow<EgressException> { client.hentUfoeregrad(pid) }.message shouldBe "My bad"
    }

    @Test
    fun `hent ufoeregrad handles server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil"))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("Feil")) // for retry

        val exception = shouldThrow<EgressException> { client.hentUfoeregrad(pid) }

        with(exception) {
            message shouldBe "Failed calling /api/uforetrygd/uforegrad/seneste"
            (cause as EgressException).message shouldBe "Feil"
        }
    }

    private companion object {
        private const val UFOEREGRAD = 80
        private const val INGEN_UFOEREGRAD = 0

        private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(RESPONSE_WITH_ACTUAL_GRAD)
        private fun okResponseWithoutUfoeregrad() = jsonResponse(HttpStatus.OK).setBody(RESPONSE_WITHOUT_GRAD)

        @Language("JSON")
        private const val RESPONSE_WITH_ACTUAL_GRAD = """
{
    "uforegrad": $UFOEREGRAD
}
"""

        @Language("JSON")
        private const val RESPONSE_WITHOUT_GRAD = """
{
    "uforegrad": null
}
"""
    }
}
