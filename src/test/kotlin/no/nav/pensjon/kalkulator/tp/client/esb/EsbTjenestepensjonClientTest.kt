package no.nav.pensjon.kalkulator.tp.client.esb

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.mock.XmlMapperFactory.xmlMapper
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.UsernameTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw.dto.UsernameTokenDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient

/**
 * ESB = Enterprise Service Bus (tjenestebuss)
 */
@ExtendWith(SpringExtension::class)
class EsbTjenestepensjonClientTest : WebClientTest() {

    private lateinit var client: EsbTjenestepensjonClient

    @Mock
    private lateinit var usernameTokenClient: UsernameTokenClient

    @BeforeEach
    fun initialize() {
        `when`(usernameTokenClient.fetchUsernameToken()).thenReturn(UsernameTokenDto(WS_SECURITY_ELEMENT))
        client = EsbTjenestepensjonClient(baseUrl(), usernameTokenClient, WebClient.create(), xmlMapper())
    }

    @Test
    fun `harTjenestepensjonsforhold returns true when forhold exists`() {
        arrangeSecurityContext()
        arrange(ettForholdResponse())

        val forholdExists = client.harTjenestepensjonsforhold(pid)

        assertTrue(forholdExists)
    }

    @Test
    fun `harTjenestepensjonsforhold returns false when ingen forhold`() {
        arrangeSecurityContext()
        arrange(ingenForholdResponse())

        val forholdExists = client.harTjenestepensjonsforhold(pid)

        assertFalse(forholdExists)
    }

    private companion object {

        private const val WS_SECURITY_ELEMENT =
            """<wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
                <wsse:UsernameToken>
                    <wsse:Username>srvpselv</wsse:Username>
                    <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">&amp;secret</wsse:Password>
                </wsse:UsernameToken>
            </wsse:Security>"""


        private const val TJENESTEPENSJONSFORHOLD_XML =
            """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:finnTjenestepensjonForholdResponse xmlns:inf="http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf">
            <finnTjenestepensjonForholdResponse>
                <fnr>12906498357</fnr>
                <tjenestepensjonForholdene>
                    <forholdId>22598178</forholdId>
                    <tssEksternId>80000470761</tssEksternId>
                    <navn>Statens pensjonskasse</navn>
                    <tpNr>3010</tpNr>
                    <harUtlandPensjon>false</harUtlandPensjon>
                    <samtykkeSimuleringKode>N</samtykkeSimuleringKode>
                    <harSimulering>false</harSimulering>
                    <tjenestepensjonYtelseListe>
                        <ytelseId>22587180</ytelseId>
                        <innmeldtFom>2014-04-01</innmeldtFom>
                        <ytelseKode>ALDER</ytelseKode>
                        <ytelseBeskrivelse>ALDER</ytelseBeskrivelse>
                        <iverksattFom>2022-09-20</iverksattFom>
                    </tjenestepensjonYtelseListe>
                    <endringsInfo>
                        <endretAvId>srvpensjon</endretAvId>
                        <opprettetAvId>srvpensjon</opprettetAvId>
                        <endretDato>2022-10-20</endretDato>
                        <opprettetDato>2022-10-20</opprettetDato>
                        <kildeId>PP01</kildeId>
                    </endringsInfo>
                    <avdelingType>TPOF</avdelingType>
                </tjenestepensjonForholdene>
                <endringsInfo>
                    <endretAvId>UNKNOWN</endretAvId>
                    <opprettetAvId>UNKNOWN</opprettetAvId>
                    <endretDato>2022-04-20</endretDato>
                    <opprettetDato>2022-04-20</opprettetDato>
                </endringsInfo>
            </finnTjenestepensjonForholdResponse>
        </inf:finnTjenestepensjonForholdResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

        private const val INGEN_TJENESTEPENSJONSFORHOLD_XML =
            """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:finnTjenestepensjonForholdResponse xmlns:inf="http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf">
            <finnTjenestepensjonForholdResponse>
                <fnr>24815797236</fnr>
                <endringsInfo>
                    <endretAvId>srvpensjon</endretAvId>
                    <opprettetAvId>srvpensjon</opprettetAvId>
                    <endretDato>2022-11-09</endretDato>
                    <opprettetDato>2022-11-09</opprettetDato>
                </endringsInfo>
            </finnTjenestepensjonForholdResponse>
        </inf:finnTjenestepensjonForholdResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

        private const val FAULT_XML = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <soapenv:Fault xmlns:m="http://schemas.xmlsoap.org/soap/envelope/">
            <faultcode>m:Server</faultcode>
            <faultstring>ServerException</faultstring>
            <detail>
                <inf:finnTjenestepensjonForhold_faultPenGenerisk xmlns:inf="http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf">
                    <errorMessage>Pid validation failed</errorMessage>
                    <errorSource>Interaction: [invoke,hentTjenestepensjonInfo]</errorSource>
                    <errorType>Runtime</errorType>
                    <rootCause>65915200188 is not a valid personal identification number</rootCause>
                    <dateTimeStamp>Tue Jun 13 15:41:29 CEST 2023</dateTimeStamp>
                </inf:finnTjenestepensjonForhold_faultPenGenerisk>
            </detail>
        </soapenv:Fault>
    </soapenv:Body>
</soapenv:Envelope>"""

        private fun ettForholdResponse() = xmlResponse().setBody(TJENESTEPENSJONSFORHOLD_XML)

        private fun ingenForholdResponse() = xmlResponse().setBody(INGEN_TJENESTEPENSJONSFORHOLD_XML)

        private fun faultResponse() = xmlResponse().setBody(FAULT_XML) //TODO fault handling
    }
}
