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
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class NorskPensjonPensjonsavtaleClientTest : WebClientTest() {

    private lateinit var client: NorskPensjonPensjonsavtaleClient

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

        client = NorskPensjonPensjonsavtaleClient(
            baseUrl = baseUrl(),
            tokenGetter = tokenService,
            webClientBuilder = webClientBuilder,
            xmlMapper = xmlMapper(),
            traceAid = traceAid,
            retryAttempts = "1"
        )
    }

    @Test
    fun `fetchAvtaler handles 1 avtale with 2 utbetalingsperioder`() {
        arrange(okResponse(EN_AVTALE_RESPONSE_BODY))

        val avtaler = client.fetchAvtaler(spec(), pid).avtaler

        assertRequestBody(EXPECTED_REQUEST_BODY)
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
    fun `fetchAvtaler handles utilstrekkelig data`() {
        arrange(okResponse(UTILSTREKKELIG_DATA_RESPONSE_BODY))
        val aarsak = client.fetchAvtaler(spec(), pid).avtaler[0].manglendeBeregningAarsak
        assertEquals(ManglendeEksternBeregningAarsak.UTILSTREKKELIG_DATA, aarsak)
    }

    @Test
    fun `fetchAvtaler handles ukjente aarsaker`() {
        arrange(okResponse(UKJENTE_AARSAKER_RESPONSE_BODY))

        val avtale = client.fetchAvtaler(spec(), pid).avtaler[0]

        with(avtale) {
            assertEquals(ManglendeEksternBeregningAarsak.UNKNOWN, manglendeBeregningAarsak)
            assertEquals(ManglendeEksternGraderingAarsak.UNKNOWN, manglendeGraderingAarsak)
        }
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

        @Language("xml")
        private const val INGEN_AVTALER_RESPONSE_BODY =
            """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="x" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://x.no/api/pensjonskalkulator/v3/typer" />
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        const val EN_AVTALE_RESPONSE_BODY =
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

        @Language("xml")
        private const val UTILSTREKKELIG_DATA_RESPONSE_BODY =
            """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-cc25c7d6-15cb-4b45-a11b-164e92644ff4" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjonskalkulator/v3/typer">
            <pensjonsRettigheter>
                <avtalenummer>3823484919</avtalenummer>
                <arbeidsgiver>981964047</arbeidsgiver>
                <selskapsnavn>Nordnet Livsforsikring AS</selskapsnavn>
                <produktbetegnelse>Egen pensjonskonto (EPK)</produktbetegnelse>
                <kategori>privatTjenestepensjon</kategori>
                <underkategori>innskuddspensjon</underkategori>
                <merknad>Norsk Pensjon mottar ikke nok data fra selskapet til &#229; beregne prognose for denne avtalen.</merknad>
                <innskuddssaldo>576259</innskuddssaldo>
                <naavaerendeAvtaltAarligInnskudd>0</naavaerendeAvtaltAarligInnskudd>
                <avkastningsgaranti>false</avkastningsgaranti>
                <beregningsmodell>norskpensjon</beregningsmodell>
                <startAlder>70</startAlder>
                <sluttAlder>80</sluttAlder>
                <utbetalingsperioder>
                    <startAlder>70</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>80</sluttAlder>
                    <sluttMaaned>1</sluttMaaned>
                    <aarligUtbetalingForventet>0</aarligUtbetalingForventet>
                    <grad>100</grad>
                </utbetalingsperioder>
                <opplysningsdato>2023-12-24</opplysningsdato>
                <aarsakIkkeBeregnet>UTILSTREKKELIG_DATA</aarsakIkkeBeregnet>
            </pensjonsRettigheter>
        </ns2:pensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        @Language("xml")
        private const val UKJENTE_AARSAKER_RESPONSE_BODY =
            """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-cc25c7d6-15cb-4b45-a11b-164e92644ff4" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjonskalkulator/v3/typer">
            <pensjonsRettigheter>
                <aarsakIkkeBeregnet>ukjent 1</aarsakIkkeBeregnet>
                <aarsakManglendeGradering>ukjent 2</aarsakManglendeGradering>
            </pensjonsRettigheter>
        </ns2:pensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""

        fun okResponse(avtale: String) = jsonResponse(HttpStatus.OK).setBody(avtale)

        fun spec() =
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = 123000,
                uttaksperioder = listOf(uttaksperiodeSpec(1), uttaksperiodeSpec(2)),
            )

        private fun uttaksperiodeSpec(value: Int) =
            UttaksperiodeSpec(
                startAlder = Alder(aar = value + 62, maaneder = value),
                grad = if (value < 2) Uttaksgrad.AATTI_PROSENT else Uttaksgrad.HUNDRE_PROSENT,
                aarligInntekt = InntektSpec(aarligBeloep = value * 100000, tomAlder = null)
            )

        private fun avtaleUtenUtbetalingsperioder() =
            Pensjonsavtale(
                avtalenummer = "",
                arbeidsgiver = "ukjent",
                selskapsnavn = "Selskap2",
                produktbetegnelse = "Produkt2",
                kategori = AvtaleKategori.FOLKETRYGD,
                underkategori = AvtaleUnderkategori.NONE,
                innskuddssaldo = 0,
                naavaerendeAvtaltAarligInnskudd = 0,
                pensjonsbeholdningForventet = 0,
                pensjonsbeholdningNedreGrense = 0,
                pensjonsbeholdningOvreGrense = 0,
                avkastningsgaranti = false,
                beregningsmodell = EksternBeregningsmodell.NONE,
                startAar = 0,
                sluttAar = null,
                opplysningsdato = "ukjent",
                manglendeGraderingAarsak = ManglendeEksternGraderingAarsak.NONE,
                manglendeBeregningAarsak = ManglendeEksternBeregningAarsak.NONE,
                utbetalingsperioder = emptyList()
            )

        private fun avtaleMedEnUtbetalingsperiode() =
            Pensjonsavtale(
                avtalenummer = "Avtale1",
                arbeidsgiver = "Firma1",
                selskapsnavn = "Selskap1",
                produktbetegnelse = "Produkt1",
                kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
                underkategori = AvtaleUnderkategori.FORENINGSKOLLEKTIV,
                innskuddssaldo = 1000,
                naavaerendeAvtaltAarligInnskudd = 100,
                pensjonsbeholdningForventet = 0,
                pensjonsbeholdningNedreGrense = 0,
                pensjonsbeholdningOvreGrense = 0,
                avkastningsgaranti = false,
                beregningsmodell = EksternBeregningsmodell.BRANSJEAVTALE,
                startAar = 70,
                sluttAar = 80,
                opplysningsdato = "2023-01-01",
                manglendeGraderingAarsak = ManglendeEksternGraderingAarsak.NONE,
                manglendeBeregningAarsak = ManglendeEksternBeregningAarsak.NONE,
                utbetalingsperioder = listOf(utbetalingsperiodeMedSluttalder())
            )

        private fun avtaleMedToUtbetalingsperioder() =
            Pensjonsavtale(
                avtalenummer = "Avtale1",
                arbeidsgiver = "Firma1",
                selskapsnavn = "Selskap1",
                produktbetegnelse = "Produkt1",
                kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
                underkategori = AvtaleUnderkategori.FORENINGSKOLLEKTIV,
                innskuddssaldo = 1000,
                naavaerendeAvtaltAarligInnskudd = 100,
                pensjonsbeholdningForventet = 1000000,
                pensjonsbeholdningNedreGrense = 900000,
                pensjonsbeholdningOvreGrense = 1100000,
                avkastningsgaranti = false,
                beregningsmodell = EksternBeregningsmodell.BRANSJEAVTALE,
                startAar = 70,
                sluttAar = 80,
                opplysningsdato = "2023-01-01",
                manglendeGraderingAarsak = ManglendeEksternGraderingAarsak.IKKE_STOETTET,
                manglendeBeregningAarsak = ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE,
                utbetalingsperioder = listOf(
                    utbetalingsperiodeMedSluttalder(),
                    utbetalingsperiodeUtenSluttalder()
                )
            )

        private fun utbetalingsperiodeMedSluttalder() =
            Utbetalingsperiode(
                startAlder = Alder(aar = 71, maaneder = 0),
                sluttAlder = Alder(aar = 81, maaneder = 1),
                aarligUtbetaling = 10000,
                grad = Uttaksgrad.HUNDRE_PROSENT
            )

        private fun utbetalingsperiodeUtenSluttalder() =
            Utbetalingsperiode(
                startAlder = Alder(aar = 72, maaneder = 1),
                sluttAlder = null,
                aarligUtbetaling = 20000,
                grad = Uttaksgrad.AATTI_PROSENT
            )

        fun assertRequestBody(body: String) {
            ByteArrayOutputStream().use {
                val request = takeRequest()
                request.body.copyTo(it)
                assertEquals(body, it.toString(StandardCharsets.UTF_8))
            }
        }
    }
}
