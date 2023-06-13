package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto.SamlTokenDataDto
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import java.nio.charset.StandardCharsets

@ExtendWith(SpringExtension::class)
class NorskPensjonPensjonsavtaleClientTest : WebClientTest() {

    private lateinit var client: NorskPensjonPensjonsavtaleClient

    @Mock
    private lateinit var samlTokenClient: SamlTokenClient

    @BeforeEach
    fun initialize() {
        `when`(samlTokenClient.fetchSamlToken()).thenReturn(samlTokenData())
        client = NorskPensjonPensjonsavtaleClient(baseUrl(), samlTokenClient, webClientForSoapRequests(), xmlMapper())
    }

    @Test
    fun `fetchAvtaler handles 1 pensjonsavtale`() {
        arrangeSecurityContext()
        arrange(okResponse(responseBody()))

        val avtaler = client.fetchAvtaler(spec())

        val avtale = avtaler.avtaler[0]
        assertEquals("PENSJONSKAPITALBEVIjS", avtale.produktbetegnelse)
        assertEquals("innskuddsbasertKollektiv", avtale.kategori)
        assertEquals(76, avtale.startAlder)
        assertEquals(86, avtale.sluttAlder)
        val utbetalingsperiode = avtale.utbetalingsperiode
        val start = utbetalingsperiode.start
        val slutt = utbetalingsperiode.slutt!!
        assertEquals(77, start.aar)
        assertEquals(1, start.maaned)
        assertEquals(87, slutt.aar)
        assertEquals(12, slutt.maaned)
        assertEquals(34000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
    }

    @Test
    fun `fetchAvtaler handles 0 pensjonsavtaler`() {
        arrangeSecurityContext()
        arrange(okResponse(ingenAvtalerResponseBody()))

        val avtaler = client.fetchAvtaler(spec())

        assertTrue(avtaler.avtaler.isEmpty())
    }

    @Test
    fun `fetchAvtaler handles utilgjengelig selskap`() {
        arrangeSecurityContext()
        arrange(okResponse(utilgjengeligeSelskapResponseBody()))

        val avtaler = client.fetchAvtaler(spec())

        val selskap = avtaler.utilgjengeligeSelskap[0]
        assertEquals("KLP Bedriftspensjon", selskap.navn)
        assertTrue(selskap.heltUtilgjengelig)
    }

    @Test
    fun `fetchAvtaler handles error response`() {
        arrangeSecurityContext()
        arrange(okResponse(errorResponseBody())) //TODO handle 500

        val exception = assertThrows(RuntimeException::class.java) { client.fetchAvtaler(spec()) }

        assertEquals("Code: soap11:Client | String: A problem occurred." +
                " | Actor: urn:nav:ikt:plattform:samhandling:q1_partner-gw-pep-sbs:OutboundDynamicSecurityGateway" +
                " | Detail: { Transaction: 4870241 | Global transaction: da10915547fa547004a2951 }",
            exception.message)
    }

    companion object {
        private fun webClientForSoapRequests() =
            WebClient.builder()
                .defaultHeaders { it.contentType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8) }
                .build()

        private fun okResponse(avtale: String) = jsonResponse(HttpStatus.OK).setBody(avtale)

        private fun samlTokenData() = SamlTokenDataDto("", "", "", 0)

        private fun spec() =
            PensjonsavtaleSpec(
                Pid("12906498357"),
                0,
                uttaksperiodeSpec(),
                0
            )

        private fun uttaksperiodeSpec() = UttaksperiodeSpec(0, 0, 0, 0)

        @Language("xml")
        private fun responseBody() = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-f7685e25-60eb-4a98-8cd3-fba5d80ea421" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <privatAlderRettigheter>
                <produktbetegnelse>PENSJONSKAPITALBEVIjS</produktbetegnelse>
                <kategori>innskuddsbasertKollektiv</kategori>
                <startAlder>76</startAlder>
                <sluttAlder>86</sluttAlder>
                <utbetalingsperioder>
                    <startAlder>77</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>87</sluttAlder>
                    <sluttMaaned>12</sluttMaaned>
                    <aarligUtbetaling>34000</aarligUtbetaling>
                    <grad>100</grad>
                </utbetalingsperioder>
            </privatAlderRettigheter>
        </ns2:privatPensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private fun ingenAvtalerResponseBody() = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-bb025e77-de80-4175-a5f7-a441051169d3" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer"/>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private fun utilgjengeligeSelskapResponseBody() = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-54ce654f-eb05-465e-bd30-36b2c081c3b8" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <utilgjengeligeSelskap>
                <navn>KLP Bedriftspensjon</navn>
                <heltUtilgjengelig>true</heltUtilgjengelig>
            </utilgjengeligeSelskap>
        </ns2:privatPensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private fun errorResponseBody() = """
<soap11:Envelope xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:soap11="http://schemas.xmlsoap.org/soap/envelope/">
    <soap11:Header>
        <wsa:Action>http://www.w3.org/2005/08/addressing/soap/fault</wsa:Action>
    </soap11:Header>
    <soap11:Body>
        <soap11:Fault>
            <faultcode>soap11:Client</faultcode>
            <faultstring>A problem occurred.</faultstring>
            <faultactor>urn:nav:ikt:plattform:samhandling:q1_partner-gw-pep-sbs:OutboundDynamicSecurityGateway</faultactor>
            <detail>
                <transaction-id>4870241</transaction-id>
                <global-transaction-id>da10915547fa547004a2951</global-transaction-id>
            </detail>
        </soap11:Fault>
    </soap11:Body>
</soap11:Envelope>
"""
    }
}
