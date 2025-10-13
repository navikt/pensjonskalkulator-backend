package no.nav.pensjon.kalkulator.avtale.client.np.v3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.EN_AVTALE_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.ERROR_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.EXPECTED_REQUEST_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.INGEN_AVTALER_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.TO_AVTALER_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.UKJENTE_AARSAKER_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.UTILGJENGELIGE_SELSKAP_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.UTILSTREKKELIG_DATA_RESPONSE_BODY
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.avtaleMedEnUtbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.avtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.v3.NorskPensjonPensjonsavtaleClientTestObjects.avtaleUtenUtbetalingsperioder
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.avtaleMedToUtbetalingsperioder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenServiceTest.Companion.SAML_ASSERTION
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.testutil.Arrange
import no.nav.pensjon.kalkulator.testutil.arrangeOkXmlResponse
import no.nav.pensjon.kalkulator.testutil.arrangeResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class NorskPensjonPensjonsavtaleClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null
    val xmlMapper = xmlMapper()
    val traceAid = mockk<TraceAid>().apply { every { callId() } returns "id1" }
    val tokenGetter = mockk<SamlTokenService>().apply { every { assertion() } returns SAML_ASSERTION }

    fun pensjonsavtaleClient(context: AssertableApplicationContext) =
        NorskPensjonPensjonsavtaleClient(
            baseUrl!!,
            tokenGetter,
            webClientBuilder = context.getBean(WebClient.Builder::class.java),
            xmlMapper,
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

    test("fetchAvtaler handles 1 avtale with 2 utbetalingsperioder") {
        server!!.arrangeOkXmlResponse(EN_AVTALE_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            val avtaler = pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid).avtaler

            avtaler.size shouldBe 1
            avtaler[0] shouldBe avtaleMedToUtbetalingsperioder

            ByteArrayOutputStream().use {
                server.takeRequest().apply { body.copyTo(it) }
                it.toString(StandardCharsets.UTF_8) shouldBe EXPECTED_REQUEST_BODY
            }
        }
    }

    test("fetchAvtaler handles 2 pensjonsavtaler with 1 utbetalingsperiode each") {
        server?.arrangeOkXmlResponse(TO_AVTALER_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            val avtaler = pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid).avtaler

            avtaler.size shouldBe 2
            avtaler[0] shouldBe avtaleMedEnUtbetalingsperiode
            avtaler[1] shouldBe avtaleUtenUtbetalingsperioder
        }
    }

    test("fetchAvtaler handles 0 pensjonsavtaler") {
        server?.arrangeOkXmlResponse(INGEN_AVTALER_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid).avtaler.isEmpty() shouldBe true
        }
    }

    test("fetchAvtaler handles utilgjengelige selskaper") {
        server?.arrangeOkXmlResponse(UTILGJENGELIGE_SELSKAP_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            val selskaper = pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid).utilgjengeligeSelskap

            selskaper.size shouldBe 2
            selskaper[0] shouldBe Selskap("Selskap1", true, 1, AvtaleKategori.PRIVAT_AFP, "Feil1")
            selskaper[1] shouldBe Selskap("Selskap2", false)
        }
    }

    test("fetchAvtaler handles utilstrekkelig data") {
        server?.arrangeOkXmlResponse(UTILSTREKKELIG_DATA_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid)
                .avtaler[0].manglendeBeregningAarsak shouldBe ManglendeEksternBeregningAarsak.UTILSTREKKELIG_DATA
        }
    }

    test("fetchAvtaler handles ukjente årsaker") {
        server?.arrangeOkXmlResponse(UKJENTE_AARSAKER_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            val avtale = pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid).avtaler[0]

            with(avtale) {
                manglendeBeregningAarsak shouldBe ManglendeEksternBeregningAarsak.UNKNOWN
                manglendeGraderingAarsak shouldBe ManglendeEksternGraderingAarsak.UNKNOWN
            }
        }
    }

    test("fetchAvtaler retries in case of server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Feil")
        server?.arrangeOkXmlResponse(INGEN_AVTALER_RESPONSE_BODY)

        Arrange.webClientContextRunner().run {
            pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid).avtaler.isEmpty() shouldBe true
        }
    }

    test("fetchAvtaler does not retry in case of client error") {
        server?.arrangeResponse(HttpStatus.BAD_REQUEST, "My bad")
        // No 2nd response arranged, since no retry

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> {
                pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid)
            }.message shouldBe "My bad"
        }
    }

    test("fetchAvtaler handles server error") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_RESPONSE_BODY)
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_RESPONSE_BODY) // for retry

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> {
                pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid)
            }.message shouldBe "Code: soap11:Client | String: A problem occurred." +
                    " | Actor: urn:nav:ikt:plattform:samhandling:q1_partner-gw-pep-sbs:OutboundDynamicSecurityGateway" +
                    " | Detail: { Transaction: 4870241 | Global transaction: da10915547fa547004a2951 }"
        }
    }

    test("fetchAvtaler handles non-XML error message") {
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, """{ "x": "y" }""")
        server?.arrangeResponse(HttpStatus.INTERNAL_SERVER_ERROR, """{ "x": "y" }""") // for retry

        Arrange.webClientContextRunner().run {
            shouldThrow<EgressException> {
                pensjonsavtaleClient(context = it).fetchAvtaler(avtaleSpec, pid)
            }.message shouldBe """Failed to call http://localhost:${server?.port}/kalkulator.pensjonsrettighetstjeneste/v3/kalkulatorPensjonTjeneste - (non-XML response) - { "x": "y" }"""
        }
    }
})

object NorskPensjonPensjonsavtaleClientTestObjects {

    val avtaleSpec =
        PensjonsavtaleSpec(
            aarligInntektFoerUttak = 123000,
            uttaksperioder = listOf(uttaksperiodeSpec(1), uttaksperiodeSpec(2)),
        )

    val avtaleUtenUtbetalingsperioder =
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

    private val utbetalingsperiodeMedSluttalder =
        Utbetalingsperiode(
            startAlder = Alder(aar = 71, maaneder = 0),
            sluttAlder = Alder(aar = 81, maaneder = 1),
            aarligUtbetaling = 10000,
            grad = Uttaksgrad.HUNDRE_PROSENT
        )

    val avtaleMedEnUtbetalingsperiode =
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
            utbetalingsperioder = listOf(utbetalingsperiodeMedSluttalder)
        )

    fun uttaksperiodeSpec(value: Int) =
        UttaksperiodeSpec(
            startAlder = Alder(aar = value + 62, maaneder = value),
            grad = if (value < 2) Uttaksgrad.AATTI_PROSENT else Uttaksgrad.HUNDRE_PROSENT,
            aarligInntekt = InntektSpec(aarligBeloep = value * 100000, tomAlder = null)
        )

    @Language("xml")
    const val EXPECTED_REQUEST_BODY = """<?xml version="1.0" ?>
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
    const val INGEN_AVTALER_RESPONSE_BODY =
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
    const val TO_AVTALER_RESPONSE_BODY =
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
    const val UTILGJENGELIGE_SELSKAP_RESPONSE_BODY = """
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
    const val ERROR_RESPONSE_BODY = """
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
    const val UTILSTREKKELIG_DATA_RESPONSE_BODY =
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
    const val UKJENTE_AARSAKER_RESPONSE_BODY =
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
}
