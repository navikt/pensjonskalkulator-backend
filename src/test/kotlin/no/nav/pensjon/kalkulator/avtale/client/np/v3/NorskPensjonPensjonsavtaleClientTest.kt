package no.nav.pensjon.kalkulator.avtale.client.np.v3

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenServiceTest.Companion.SAML_ASSERTION
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.*

@ExtendWith(SpringExtension::class)
class NorskPensjonPensjonsavtaleClientTest : WebClientTest() {

    private lateinit var client: NorskPensjonPensjonsavtaleClient

    @Mock
    private lateinit var tokenService: SamlTokenService

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        `when`(tokenService.assertion()).thenReturn(SAML_ASSERTION)
        `when`(traceAid.callId()).thenReturn("id1")
        arrangeSecurityContext()

        client = NorskPensjonPensjonsavtaleClient(
            baseUrl = baseUrl(),
            tokenGetter = tokenService,
            webClient = WebClientConfig().webClientForSoapRequests(),
            xmlMapper = xmlMapper(),
            traceAid = traceAid,
            retryAttempts = "1"
        )
    }

    @Test
    fun `fetchAvtaler handles 1 avtale with 2 utbetalingsperioder`() {
        arrange(okResponse(EN_AVTALE_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec(), pid).avtaler

        assertRequestBody()
        assertEquals(1, avtaler.size)
        avtaler[0] shouldBe avtaleMedToUtbetalingsperioder()
    }

    @Test
    fun `fetchAvtaler handles 2 pensjonsavtaler with 1 utbetalingsperiode each`() {
        arrange(okResponse(TO_AVTALER_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec(), pid).avtaler

        assertEquals(2, avtaler.size)
        avtaler[0] shouldBe avtaleMedEnUtbetalingsperiode()
        avtaler[1] shouldBe avtaleUtenUtbetalingsperioder()
    }

    @Test
    fun `fetchAvtaler handles 0 pensjonsavtaler`() {
        arrange(okResponse(INGEN_AVTALER_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec(), pid).avtaler

        assertTrue(avtaler.isEmpty())
    }

    @Test
    fun `fetchAvtaler handles utilgjengelige selskaper`() {
        arrange(okResponse(UTILGJENGELIGE_SELSKAP_RESPONSE_BODY))

        val selskaper = client.fetchAvtaler(spec(), pid).utilgjengeligeSelskap

        assertEquals(2, selskaper.size)
        selskaper[0] shouldBe Selskap("Selskap1", true, 1, AvtaleKategori.PRIVAT_AFP, "Feil1")
        selskaper[1] shouldBe Selskap("Selskap2", false)
    }

    @Test
    fun `fetchAvtaler retries in case of server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody(ERROR_RESPONSE_BODY))
        arrange(okResponse(INGEN_AVTALER_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec(), pid).avtaler

        assertTrue(avtaler.isEmpty())
    }

    @Test
    fun `fetchAvtaler does not retry in case of client error`() {
        arrange(jsonResponse(HttpStatus.BAD_REQUEST).setBody("My bad"))
        // No 2nd response arranged, since no retry

        val exception = assertThrows<EgressException> { client.fetchAvtaler(spec(), pid) }

        assertEquals("My bad", exception.message)
    }

    @Test
    fun `fetchAvtaler handles server error`() {
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody(ERROR_RESPONSE_BODY))
        arrange(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody(ERROR_RESPONSE_BODY)) // for retry

        val exception = assertThrows<EgressException> { client.fetchAvtaler(spec(), pid) }

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
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:typ="http://norskpensjon.no/api/pensjonskalkulator/v3/typer">
    <S:Header>
        $SAML_ASSERTION
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
                <antallInntektsaarEtterUttak>1</antallInntektsaarEtterUttak>
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

        @Language("xml")
        private const val INGEN_AVTALER_RESPONSE_BODY =
            """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="x" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://x.no/api/pensjonskalkulator/v3/typer" />
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val EN_AVTALE_RESPONSE_BODY =
            """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="x" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://x.no/api/pensjonskalkulator/v3/typer">
            <pensjonsRettigheter>
                <avtalenummer>Avtale1</avtalenummer>
                <arbeidsgiver>Firma1</arbeidsgiver>
                <selskapsnavn>Selskap1</selskapsnavn>
                <produktbetegnelse>Produkt1</produktbetegnelse>
                <kategori>individuelleOrdninger</kategori>
                <underkategori>foreningskollektiv</underkategori>
                <innskuddssaldo>1000</innskuddssaldo>
                <naavaerendeAvtaltAarligInnskudd>100</naavaerendeAvtaltAarligInnskudd>
                <pensjonsbeholdningForventet>1000000</pensjonsbeholdningForventet>
                <pensjonsbeholdningNedreGrense>900000</pensjonsbeholdningNedreGrense>
                <pensjonsbeholdningOvreGrense>1100000</pensjonsbeholdningOvreGrense>
                <avkastningsgaranti>false</avkastningsgaranti>
                <beregningsmodell>bransjeavtale</beregningsmodell>
                <startAlder>70</startAlder>
                <sluttAlder>80</sluttAlder>
                <opplysningsdato>2023-01-01</opplysningsdato>
                <aarsakManglendeGradering>IKKE_STOTTET</aarsakManglendeGradering>
                <aarsakIkkeBeregnet>UKJENT_PRODUKTTYPE</aarsakIkkeBeregnet>
                <utbetalingsperioder>
                    <startAlder>71</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>81</sluttAlder>
                    <sluttMaaned>3</sluttMaaned>
                    <aarligUtbetalingForventet>10000</aarligUtbetalingForventet>
                    <grad>100</grad>
                </utbetalingsperioder>
                <utbetalingsperioder>
                    <startAlder>72</startAlder>
                    <startMaaned>2</startMaaned>
                    <aarligUtbetalingForventet>20000</aarligUtbetalingForventet>
                    <grad>80</grad>
                </utbetalingsperioder>
            </pensjonsRettigheter>
        </ns2:pensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val TO_AVTALER_RESPONSE_BODY =
            """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="x" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://x.no/api/pensjonskalkulator/v3/typer">
            <pensjonsRettigheter>
                <avtalenummer>Avtale1</avtalenummer>
                <arbeidsgiver>Firma1</arbeidsgiver>
                <selskapsnavn>Selskap1</selskapsnavn>
                <produktbetegnelse>Produkt1</produktbetegnelse>
                <kategori>individuelleOrdninger</kategori>
                <underkategori>foreningskollektiv</underkategori>
                <innskuddssaldo>1000</innskuddssaldo>
                <naavaerendeAvtaltAarligInnskudd>100</naavaerendeAvtaltAarligInnskudd>
                <avkastningsgaranti>false</avkastningsgaranti>
                <beregningsmodell>bransjeavtale</beregningsmodell>
                <startAlder>70</startAlder>
                <sluttAlder>80</sluttAlder>
                <utbetalingsperioder>
                    <startAlder>71</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>81</sluttAlder>
                    <sluttMaaned>3</sluttMaaned>
                    <aarligUtbetalingForventet>10000</aarligUtbetalingForventet>
                    <grad>100</grad>
                </utbetalingsperioder>
                <opplysningsdato>2023-01-01</opplysningsdato>
            </pensjonsRettigheter>
            <pensjonsRettigheter>
                <selskapsnavn>Selskap2</selskapsnavn>
                <produktbetegnelse>Produkt2</produktbetegnelse>
                <kategori>Folketrygd</kategori>
            </pensjonsRettigheter>
        </ns2:pensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val UTILGJENGELIGE_SELSKAP_RESPONSE_BODY = """
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-54ce654f-eb05-465e-bd30-36b2c081c3b8" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://x.no/api/pensjonskalkulator/v3/typer">
            <utilgjengeligeInnretninger>
                <selskapsnavn>Selskap1</selskapsnavn>
                <heltUtilgjengelig>true</heltUtilgjengelig>
                <antallManglendeRettigheter>1</antallManglendeRettigheter>
                <kategori>privatAFP</kategori>
                <feilkode>Feil1</feilkode>
            </utilgjengeligeInnretninger>
            <utilgjengeligeInnretninger>
                <selskapsnavn>Selskap2</selskapsnavn>
                <heltUtilgjengelig>false</heltUtilgjengelig>
            </utilgjengeligeInnretninger>
        </ns2:pensjonsrettigheter>
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

        private fun okResponse(avtale: String) = jsonResponse(HttpStatus.OK).setBody(avtale)

        private fun spec() =
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = 123000,
                uttaksperioder = listOf(uttaksperiodeSpec(1), uttaksperiodeSpec(2)),
                antallInntektsaarEtterUttak = 1
            )

        private fun uttaksperiodeSpec(value: Int) =
            UttaksperiodeSpec(
                Alder(value + 62, value),
                if (value < 2) Uttaksgrad.AATTI_PROSENT else Uttaksgrad.HUNDRE_PROSENT,
                value * 100000
            )

        private fun avtaleUtenUtbetalingsperioder() =
            Pensjonsavtale(
                "",
                "ukjent",
                "Selskap2",
                "Produkt2",
                AvtaleKategori.FOLKETRYGD,
                AvtaleUnderkategori.NONE,
                0,
                0,
                0,
                0,
                0,
                false,
                EksternBeregningsmodell.NONE,
                0,
                null,
                "ukjent",
                ManglendeEksternGraderingAarsak.NONE,
                ManglendeEksternBeregningAarsak.NONE,
                emptyList()
            )

        private fun avtaleMedEnUtbetalingsperiode() =
            Pensjonsavtale(
                "Avtale1",
                "Firma1",
                "Selskap1",
                "Produkt1",
                AvtaleKategori.INDIVIDUELL_ORDNING,
                AvtaleUnderkategori.FORENINGSKOLLEKTIV,
                1000,
                100,
                0,
                0,
                0,
                false,
                EksternBeregningsmodell.BRANSJEAVTALE,
                70,
                80,
                "2023-01-01",
                ManglendeEksternGraderingAarsak.NONE,
                ManglendeEksternBeregningAarsak.NONE,
                listOf(utbetalingsperiodeMedSluttalder())
            )

        private fun avtaleMedToUtbetalingsperioder() =
            Pensjonsavtale(
                "Avtale1",
                "Firma1",
                "Selskap1",
                "Produkt1",
                AvtaleKategori.INDIVIDUELL_ORDNING,
                AvtaleUnderkategori.FORENINGSKOLLEKTIV,
                1000,
                100,
                1000000,
                900000,
                1100000,
                false,
                EksternBeregningsmodell.BRANSJEAVTALE,
                70,
                80,
                "2023-01-01",
                ManglendeEksternGraderingAarsak.IKKE_STOETTET,
                ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE,
                listOf(
                    utbetalingsperiodeMedSluttalder(),
                    utbetalingsperiodeUtenSluttalder()
                )
            )

        private fun utbetalingsperiodeMedSluttalder() =
            Utbetalingsperiode(
                Alder(71, 0),
                Alder(81, 1),
                10000,
                Uttaksgrad.HUNDRE_PROSENT
            )

        private fun utbetalingsperiodeUtenSluttalder() =
            Utbetalingsperiode(
                Alder(72, 1),
                null,
                20000,
                Uttaksgrad.AATTI_PROSENT
            )

        private fun assertRequestBody() {
            ByteArrayOutputStream().use {
                val request = takeRequest()
                request.body.copyTo(it)
                assertEquals(EXPECTED_REQUEST_BODY, it.toString(StandardCharsets.UTF_8))
            }
        }
    }
}
