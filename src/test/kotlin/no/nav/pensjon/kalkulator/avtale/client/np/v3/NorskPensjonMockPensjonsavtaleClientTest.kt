package no.nav.pensjon.kalkulator.avtale.client.np.v3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonMockPensjonsavtaleClientTestObjects.BODY_WITHOUT_HEADER
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.EN_AVTALE_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.avtaleSpec
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.avtaleMedToUtbetalingsperioder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenServiceTest.Companion.SAML_ASSERTION
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkXmlResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class NorskPensjonMockPensjonsavtaleClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val xmlMapper = xmlMapper()
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }
    val tokenGetter = mockk<SamlTokenService>().apply { every { assertion() } returns SAML_ASSERTION }

    fun pensjonsavtaleClient(context: AssertableApplicationContext) =
        NorskPensjonMockPensjonsavtaleClient(
            baseUrl!!,
            tokenGetter,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            traceAid,
            xmlMapper,
            retryAttempts = "1"
        )

    beforeSpec {
        Arrange.security()
        server = MockWebServer().also { it.start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("fetchAvtaler handles 1 avtale with 2 utbetalingsperioder & request has SOAP header without SAML") {
        server!!.arrangeOkXmlResponse(EN_AVTALE_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            val avtaler = pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid).avtaler

            avtaler.size shouldBe 1
            avtaler[0] shouldBe avtaleMedToUtbetalingsperioder

            ByteArrayOutputStream().use {
                server.takeRequest().apply { body.copyTo(it) }
                it.toString(StandardCharsets.UTF_8) shouldBe BODY_WITHOUT_HEADER
            }
        }
    }
})

object NorskPensjonMockPensjonsavtaleClientTestObjects {

    @Language("xml")
    const val BODY_WITHOUT_HEADER = """<?xml version="1.0" ?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:typ="http://norskpensjon.no/api/pensjonskalkulator/v3/typer">
    <S:Header>
    </S:Header>
    <S:Body>
        <typ:kalkulatorForespoersel>
            <userSessionCorrelationID>id1</userSessionCorrelationID>
            <organisasjonsnummer>889640782</organisasjonsnummer>
            <rettighetshaver>
                <foedselsnummer>12906498357</foedselsnummer>
                <aarligInntektFoerUttak>123000</aarligInntektFoerUttak>
                <uttaksperiode>
                    <startAlder>63</startAlder>
                    <startMaaned>2</startMaaned>
                    <grad>80</grad>
                    <aarligInntekt>100000</aarligInntekt>
                </uttaksperiode><uttaksperiode>
                    <startAlder>64</startAlder>
                    <startMaaned>3</startMaaned>
                    <grad>100</grad>
                    <aarligInntekt>200000</aarligInntekt>
                </uttaksperiode>
                <antallInntektsaarEtterUttak>13</antallInntektsaarEtterUttak>
                <harAfp>false</harAfp>
                <antallAarIUtlandetEtter16>0</antallAarIUtlandetEtter16>
                <sivilstatus>gift</sivilstatus>
                <harEpsPensjon>true</harEpsPensjon>
                <harEpsPensjonsgivendeInntektOver2G>true</harEpsPensjonsgivendeInntektOver2G>
                <oenskesSimuleringAvFolketrygd>false</oenskesSimuleringAvFolketrygd>
            </rettighetshaver>
        </typ:kalkulatorForespoersel>
    </S:Body>
</S:Envelope>"""
}
