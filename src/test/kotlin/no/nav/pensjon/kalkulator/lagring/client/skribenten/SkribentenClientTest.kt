package no.nav.pensjon.kalkulator.lagring.client.skribenten

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.mockk
import no.nav.pensjon.kalkulator.lagring.LagreAlderspensjon
import no.nav.pensjon.kalkulator.lagring.LagreAlder
import no.nav.pensjon.kalkulator.lagring.LagrePensjonsopptjening
import no.nav.pensjon.kalkulator.lagring.LagreServiceberegning
import no.nav.pensjon.kalkulator.lagring.LagreSimulering
import no.nav.pensjon.kalkulator.lagring.LagreTidsbegrensetOffentligAfp
import no.nav.pensjon.kalkulator.lagring.LagreVilkaarsproevingsresultat
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkJsonResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

class SkribentenClientTest : ShouldSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val traceAid = mockk<TraceAid>(relaxed = true)

    fun client(context: BeanFactory) =
        SkribentenClient(
            baseUrl = baseUrl!!,
            webClientBuilder = context.getBean<WebClient.Builder>(),
            traceAid = traceAid,
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

    should("returnere brev-respons ved vellykket lagring") {
        server?.arrangeOkJsonResponse(BREV_RESPONSE)

        Arrange.webClientContextRunner().run {
            val response = client(it).lagreSimulering(SAK_ID, simulering(), null)

            response.brevId shouldBe "123"
            response.sakId shouldBe "456"
        }
    }

    should("sende request til korrekt URI med sak-ID") {
        server?.arrangeOkJsonResponse(BREV_RESPONSE)

        Arrange.webClientContextRunner().run {
            client(it).lagreSimulering(SAK_ID, simulering(), null)

            server?.takeRequest()?.let { request ->
                request.path shouldBe "/external/api/v1/brev"
                request.body.readUtf8() shouldContain "\"pensjonsopptjeningListe\":[{\"aarstall\":2024,\"pensjonsgivendeInntekt\":600000,\"pensjonspoeng\":5.2,\"pensjonsbeholdning\":1000000,\"merknad\":\"Omsorgspoeng\"}]"
            }
        }
    }

    should("sende serviceberegningbrev når serviceberegning har AFP") {
        server?.arrangeOkJsonResponse(BREV_RESPONSE)

        Arrange.webClientContextRunner().run {
            client(it).lagreSimulering(SAK_ID, simulering().copy(serviceberegning = serviceberegning()), null)

            server?.takeRequest()?.let { request ->
                val body = request.body.readUtf8()
                body shouldContain "\"brevkode\":\"SERVICEBEREGNING\""
                body shouldContain "\"uttaksalder\":{\"aar\":62,\"maaneder\":0}"
                body shouldContain "\"forventetFremtidigInntekt\":500000"
                body shouldContain "\"afp\":{\"alderAar\":62"
            }
        }
    }

    should("kaste EgressException ved serverfeil") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR)
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR) // retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> {
                client(it).lagreSimulering(SAK_ID, simulering(), null)
            }

            exception.isClientError shouldBe false
        }
    }

    should("kaste EgressException med isClientError ved 4xx-feil") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, CLIENT_ERROR)
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, CLIENT_ERROR) // retry

        Arrange.webClientContextRunner().run {
            val exception = shouldThrow<EgressException> {
                client(it).lagreSimulering(SAK_ID, simulering(), null)
            }

            exception.isClientError shouldBe true
        }
    }
}) {
    companion object {
        private const val SAK_ID = 42L

        @Language("json")
        private val BREV_RESPONSE = """{
            "brevId": 123,
            "sakId": 456
        }"""

        @Language("json")
        private val SERVER_ERROR = """{
            "status": 500,
            "error": "Internal Server Error"
        }"""

        @Language("json")
        private val CLIENT_ERROR = """{
            "status": 400,
            "error": "Bad Request"
        }"""

        private fun simulering() = LagreSimulering(
            alderspensjonListe = listOf(LagreAlderspensjon(alderAar = 67, beloep = 250000, gjenlevendetillegg = null)),
            afpPrivat = null,
            afpOffentligLivsvarig = null,
            afpOffentligTidsbegrenset = null,
            vilkaarsproevingsresultat = LagreVilkaarsproevingsresultat(erInnvilget = true, alternativ = null),
            trygdetid = null,
            pensjonsgivendeInntektListe = emptyList(),
            aarligInntektOgPensjonListe = null,
            pensjonsopptjeningListe = listOf(
                LagrePensjonsopptjening(
                    aarstall = 2024,
                    pensjonsgivendeInntekt = 600000,
                    pensjonspoeng = 5.2,
                    pensjonsbeholdning = 1000000,
                    merknad = "Omsorgspoeng",
                )
            ),
            simuleringsinformasjon = null,
            maanedligAlderspensjonForKnekkpunkter = null,
            enhetsId = "4817"
        )

        private fun serviceberegning() = LagreServiceberegning(
            uttaksalder = LagreAlder(aar = 62, maaneder = 0),
            uttaksdato = "2028-01-01",
            forventetFremtidigInntekt = 500000,
            afp = LagreTidsbegrensetOffentligAfp(
                alderAar = 62,
                totaltAfpBeloep = 100000,
                tidligereArbeidsinntekt = 600000,
                grunnbeloep = 124028,
                sluttpoengtall = 4.2,
                trygdetid = 40,
                poengaarTom1991 = 10,
                poengaarFom1992 = 20,
                grunnpensjon = 40000,
                tilleggspensjon = 50000,
                afpTillegg = 10000,
                saertillegg = 0,
                afpGrad = 100,
                erAvkortet = false,
            ),
        )
    }
}
