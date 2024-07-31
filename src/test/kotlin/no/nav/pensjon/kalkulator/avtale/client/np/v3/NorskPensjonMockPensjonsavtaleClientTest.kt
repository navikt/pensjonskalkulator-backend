package no.nav.pensjon.kalkulator.avtale.client.np.v3

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTest.Companion.EN_AVTALE_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTest.Companion.assertRequestBody
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTest.Companion.okResponse
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTest.Companion.spec
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.avtaleMedToUtbetalingsperioder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenServiceTest.Companion.SAML_ASSERTION
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class NorskPensjonMockPensjonsavtaleClientTest : WebClientTest() {

    private lateinit var client: NorskPensjonMockPensjonsavtaleClient

    @Mock
    private lateinit var tokenService: SamlTokenService

    @Mock
    private lateinit var traceAid: TraceAid

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @BeforeEach
    fun initialize() {
        `when`(tokenService.assertion()).thenReturn(SAML_ASSERTION)
        `when`(traceAid.callId()).thenReturn("id1")
        arrangeSecurityContext()

        client = NorskPensjonMockPensjonsavtaleClient(
            mockUrl = baseUrl(),
            tokenGetter = tokenService,
            webClientBuilder = webClientBuilder,
            xmlMapper = xmlMapper(),
            traceAid = traceAid,
            retryAttempts = "1"
        )
    }

    @Test
    fun `fetchAvtaler handles 1 avtale with 2 utbetalingsperioder & request has a Header without SAML`() {
        arrange(okResponse(EN_AVTALE_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec(), pid).avtaler

        assertRequestBody(EXPECTED_REQUEST_BODY)
        assertEquals(1, avtaler.size)
        avtaler[0] shouldBe avtaleMedToUtbetalingsperioder()
    }

    companion object {

        @Language("xml")
        private const val EXPECTED_REQUEST_BODY = """<?xml version="1.0" ?>
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
}