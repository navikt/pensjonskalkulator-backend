package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto.SamlTokenDataDto
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
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
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

@ExtendWith(SpringExtension::class)
class NorskPensjonPensjonsavtaleClientTest : WebClientTest() {

    private lateinit var client: NorskPensjonPensjonsavtaleClient

    @Mock
    private lateinit var samlTokenClient: SamlTokenClient

    @Mock
    private lateinit var callIdGenerator: CallIdGenerator


    @BeforeEach
    fun initialize() {
        `when`(samlTokenClient.fetchSamlToken()).thenReturn(samlTokenData())
        `when`(callIdGenerator.newId()).thenReturn("id1")

        client = NorskPensjonPensjonsavtaleClient(
            baseUrl(),
            samlTokenClient,
            webClientForSoapRequests(),
            xmlMapper(),
            callIdGenerator
        )
    }

    @Test
    fun `fetchAvtaler handles 1 avtale with 2 utbetalingsperioder`() {
        arrangeSecurityContext()
        arrange(okResponse(EN_AVTALE_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec()).avtaler

        assertRequestBody()
        assertEquals(1, avtaler.size)
        assertAvtale(avtaleMedToUtbetalingsperioder(), avtaler[0])
    }

    @Test
    fun `fetchAvtaler handles 2 pensjonsavtaler with 1 utbetalingsperiode each`() {
        arrangeSecurityContext()
        arrange(okResponse(TO_AVTALER_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec()).avtaler

        assertEquals(2, avtaler.size)
        assertAvtale(avtaleMedEnUtbetalingsperiode(), avtaler[0])
        assertAvtale(avtaleUtenUtbetalingsperioder(), avtaler[1])
    }

    @Test
    fun `fetchAvtaler handles 0 pensjonsavtaler`() {
        arrangeSecurityContext()
        arrange(okResponse(INGEN_AVTALER_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec()).avtaler

        assertTrue(avtaler.isEmpty())
    }

    @Test
    fun `fetchAvtaler handles utilgjengelige selskaper`() {
        arrangeSecurityContext()
        arrange(okResponse(UTILGJENGELIGE_SELSKAP_RESPONSE_BODY))

        val selskaper = client.fetchAvtaler(spec()).utilgjengeligeSelskap

        assertEquals(2, selskaper.size)
        assertSelskap(Selskap("KLP Bedriftspensjon", true), selskaper[0])
        assertSelskap(Selskap("SPK", false), selskaper[1])
    }

    @Test
    fun `fetchAvtaler handles error response`() {
        arrangeSecurityContext()
        arrange(okResponse(ERROR_RESPONSE_BODY)) //TODO handle 500

        val exception = assertThrows(RuntimeException::class.java) { client.fetchAvtaler(spec()) }

        assertEquals(
            "Code: soap11:Client | String: A problem occurred." +
                    " | Actor: urn:nav:ikt:plattform:samhandling:q1_partner-gw-pep-sbs:OutboundDynamicSecurityGateway" +
                    " | Detail: { Transaction: 4870241 | Global transaction: da10915547fa547004a2951 }",
            exception.message
        )
    }

    companion object {

        @Language("xml")
        private const val EXPECTED_REQUEST_BODY = """<?xml version="1.0" ?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header>
        <callId xmlns="uri:no.nav.applikasjonsrammeverk">id1</callId>
        <sc:StelvioContext xmlns="" xmlns:sc="http://www.nav.no/StelvioContextPropagation">
            <applicationId>PP01</applicationId>
            <correlationId>id1</correlationId>
            <userId>12906498357</userId>
        </sc:StelvioContext>
        
    </SOAP-ENV:Header>
    <S:Body>
        <np:rettighetshaver xmlns:np="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <foedselsnummer xmlns="">12906498357</foedselsnummer>
            <aarligInntektFoerUttak xmlns="">0</aarligInntektFoerUttak>
            <uttaksperiode xmlns="">
                <startAlder>1</startAlder>
                <startMaaned>1</startMaaned>
                <grad>1</grad>
                <aarligInntekt>1</aarligInntekt>
            </uttaksperiode><uttaksperiode xmlns="">
                <startAlder>2</startAlder>
                <startMaaned>2</startMaaned>
                <grad>2</grad>
                <aarligInntekt>2</aarligInntekt>
            </uttaksperiode>
            <antallInntektsaarEtterUttak xmlns="">0</antallInntektsaarEtterUttak>
        </np:rettighetshaver>
    </S:Body>
</S:Envelope>"""

        @Language("xml")
        private const val INGEN_AVTALER_RESPONSE_BODY = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-bb025e77-de80-4175-a5f7-a441051169d3" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer"/>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val EN_AVTALE_RESPONSE_BODY = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-f7685e25-60eb-4a98-8cd3-fba5d80ea421" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <privatAlderRettigheter>
                <produktbetegnelse>PENSJONSKAPITALBEVIjS</produktbetegnelse>
                <kategori>innskuddsbasertKollektiv</kategori>
                <startAlder>70</startAlder>
                <sluttAlder>80</sluttAlder>
                <utbetalingsperioder>
                    <startAlder>71</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>81</sluttAlder>
                    <sluttMaaned>2</sluttMaaned>
                    <aarligUtbetaling>10000</aarligUtbetaling>
                    <grad>100</grad>
                </utbetalingsperioder>
                <utbetalingsperioder>
                    <startAlder>72</startAlder>
                    <startMaaned>2</startMaaned>
                    <aarligUtbetaling>20000</aarligUtbetaling>
                    <grad>50</grad>
                </utbetalingsperioder>
            </privatAlderRettigheter>
        </ns2:privatPensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val TO_AVTALER_RESPONSE_BODY = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-f7685e25-60eb-4a98-8cd3-fba5d80ea421" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <privatAlderRettigheter>
                <produktbetegnelse>PENSJONSKAPITALBEVIjS</produktbetegnelse>
                <kategori>innskuddsbasertKollektiv</kategori>
                <startAlder>70</startAlder>
                <sluttAlder>80</sluttAlder>
                <utbetalingsperioder>
                    <startAlder>71</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>81</sluttAlder>
                    <sluttMaaned>2</sluttMaaned>
                    <aarligUtbetaling>10000</aarligUtbetaling>
                    <grad>100</grad>
                </utbetalingsperioder>
            </privatAlderRettigheter>
            <privatAlderRettigheter>
                <produktbetegnelse>AFP</produktbetegnelse>
                <kategori>ytelsesbasertIndividuell</kategori>
                <startAlder>75</startAlder>
                <sluttAlder>85</sluttAlder>
            </privatAlderRettigheter>
        </ns2:privatPensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val UTILGJENGELIGE_SELSKAP_RESPONSE_BODY = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-54ce654f-eb05-465e-bd30-36b2c081c3b8" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <utilgjengeligeSelskap>
                <navn>KLP Bedriftspensjon</navn>
                <heltUtilgjengelig>true</heltUtilgjengelig>
            </utilgjengeligeSelskap>
            <utilgjengeligeSelskap>
                <navn>SPK</navn>
                <heltUtilgjengelig>false</heltUtilgjengelig>
            </utilgjengeligeSelskap>
        </ns2:privatPensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val ERROR_RESPONSE_BODY = """
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
                listOf(uttaksperiodeSpec(1), uttaksperiodeSpec(2)),
                0
            )

        private fun uttaksperiodeSpec(value: Int) = UttaksperiodeSpec(value, value, value, value)

        private fun avtaleUtenUtbetalingsperioder() =
            Pensjonsavtale(
                "AFP",
                "ytelsesbasertIndividuell",
                75,
                85,
                emptyList()
            )

        private fun avtaleMedEnUtbetalingsperiode() =
            Pensjonsavtale(
                "PENSJONSKAPITALBEVIjS",
                "innskuddsbasertKollektiv",
                70,
                80,
                listOf(utbetalingsperiodeMedSluttalder())
            )

        private fun avtaleMedToUtbetalingsperioder() =
            Pensjonsavtale(
                "PENSJONSKAPITALBEVIjS",
                "innskuddsbasertKollektiv",
                70,
                80,
                listOf(
                    utbetalingsperiodeMedSluttalder(),
                    utbetalingsperiodeUtenSluttalder()
                )
            )

        private fun utbetalingsperiodeMedSluttalder() =
            Utbetalingsperiode(
                Alder(71, 1),
                Alder(81, 2),
                10000,
                100
            )

        private fun utbetalingsperiodeUtenSluttalder() =
            Utbetalingsperiode(
                Alder(72, 2),
                null,
                20000,
                50
            )

        private fun assertAvtale(expected: Pensjonsavtale, actual: Pensjonsavtale) {
            assertEquals(expected.kategori, actual.kategori)
            assertEquals(expected.produktbetegnelse, actual.produktbetegnelse)
            assertEquals(expected.startAlder, actual.startAlder)
            assertEquals(expected.sluttAlder, actual.sluttAlder)
            assertEquals(expected.utbetalingsperioder.size, actual.utbetalingsperioder.size)

            if (actual.utbetalingsperioder.isNotEmpty()) {
                assertUtbetalingsperiode(expected.utbetalingsperioder[0], actual.utbetalingsperioder[0])
            }
        }

        private fun assertUtbetalingsperiode(expected: Utbetalingsperiode, actual: Utbetalingsperiode) {
            assertAlder(expected.start, actual.start)
            assertAlder(expected.slutt, actual.slutt)
            assertEquals(expected.aarligUtbetaling, actual.aarligUtbetaling)
            assertEquals(expected.grad, actual.grad)
        }

        private fun assertAlder(expected: Alder?, actual: Alder?) {
            assertEquals(expected?.aar, actual?.aar)
            assertEquals(expected?.maaned, actual?.maaned)
        }

        private fun assertSelskap(expected: Selskap, actual: Selskap) {
            assertEquals(expected.navn, actual.navn)
            assertEquals(expected.heltUtilgjengelig, actual.heltUtilgjengelig)
        }

        private fun assertRequestBody() {
            ByteArrayOutputStream().use {
                val request = takeRequest()
                request.body.copyTo(it)
                assertEquals(EXPECTED_REQUEST_BODY, it.toString(StandardCharsets.UTF_8))
            }
        }
    }
}
