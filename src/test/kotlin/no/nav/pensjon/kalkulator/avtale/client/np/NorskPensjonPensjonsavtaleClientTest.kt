package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
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
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
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
        client = NorskPensjonPensjonsavtaleClient(baseUrl(), samlTokenClient, webClientForSoapRequests())
    }

    @Test
    fun `fetchAvtaler handles single pensjonsavtale`() {
        arrangeSecurityContext()
        arrange(okResponse(responseBody()))

        val response = client.fetchAvtaler(spec())

        assertEquals("PENSJONSKAPITALBEVIjS", response.produktbetegnelse)
        assertEquals("innskuddsbasertKollektiv", response.kategori)
        assertEquals(76, response.startAlder)
        assertEquals(86, response.sluttAlder)
        val utbetalingsperiode = response.utbetalingsperiode
        val start = utbetalingsperiode.start
        val slutt = utbetalingsperiode.slutt!!
        assertEquals(77, start.aar)
        assertEquals(1, start.maaned)
        assertEquals(87, slutt.aar)
        assertEquals(12, slutt.maaned)
        assertEquals(34000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
    }

    companion object {
        private fun webClientForSoapRequests(): WebClient {
            return WebClient.builder()
                .defaultHeaders { it.contentType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8) }
                .build()
        }

        private fun arrangeSecurityContext() {
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

            SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
                TestingAuthenticationToken("TEST_USER", null),
                EgressTokenSuppliersByService(mapOf())
            )
        }

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
    }
}
