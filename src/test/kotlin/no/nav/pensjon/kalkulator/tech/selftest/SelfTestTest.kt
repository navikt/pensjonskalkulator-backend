package no.nav.pensjon.kalkulator.tech.selftest

import no.nav.pensjon.kalkulator.common.client.fssgw.FssGatewayPingClient
import no.nav.pensjon.kalkulator.common.client.pen.PenPingClient
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.PensjonReglerGrunnbeloepClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.PoppOpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.client.pdl.PdlPersonClient
import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdOAuth2MetadataClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.nom.NomSkjermingClient
import no.nav.pensjon.kalkulator.tech.security.ingress.ping.IdPortenPingClient
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClient
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalTime

@ExtendWith(SpringExtension::class)
class SelfTestTest {

    private lateinit var selfTest: SelfTest

    @Mock
    private lateinit var fssGatewayClient: FssGatewayPingClient

    @Mock
    private lateinit var grunnbeloepClient: PensjonReglerGrunnbeloepClient

    @Mock
    private lateinit var entraIdClient: AzureAdOAuth2MetadataClient

    @Mock
    private lateinit var idPortenClient: IdPortenPingClient

    @Mock
    private lateinit var opptjeningClient: PoppOpptjeningsgrunnlagClient

    @Mock
    private lateinit var penClient: PenPingClient

    @Mock
    private lateinit var personClient: PdlPersonClient

    @Mock
    private lateinit var skjermingClient: NomSkjermingClient

    @Mock
    private lateinit var tjenestepensjonClient: TpTjenestepensjonClient

    @BeforeEach
    fun initialize() {
        selfTest = TestClass(
            fssGatewayClient,
            grunnbeloepClient,
            idPortenClient,
            entraIdClient,
            opptjeningClient,
            penClient,
            personClient,
            skjermingClient,
            tjenestepensjonClient
        )
    }

    @Test
    fun `when services are up then performSelfTestAndReportAsHtml returns HTML indicating up`() {
        arrangeUp()

        val html = selfTest.performSelfTestAndReportAsHtml()

        assertEquals(
            """<!DOCTYPE html>
<html>
<head>
<title>pensjonskalkulator-backend selvtest</title>
<style type="text/css">
table {border-collapse: collapse; font-family: Tahoma, Geneva, sans-serif;}
table td {padding: 15px;}
table thead th {padding: 15px; background-color: #54585d; color: #ffffff; font-weight: bold; font-size: 13px; border: 1px solid #54585d;}
table tbody td {border: 1px solid #dddfe1;}
table tbody tr {background-color: #f9fafb;}
table tbody tr:nth-child(odd) {background-color: #ffffff;}
</style>
</head>
<body>
<div>
<table>
<thead>
<tr>
<th>Tjeneste</th><th>Status</th><th>Informasjon</th><th>Endepunkt</th><th>Beskrivelse</th>
</tr>
</thead>
$TABLE_BODY
</table>
</div>
</body>
</html>""", html
        )
    }

    @Test
    fun `when services are down then performSelfTestAndReportAsJson returns JSON containing error message`() {
        arrangeDown()

        val json = selfTest.performSelfTestAndReportAsJson()

        assertEquals(
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":1,"checks":$DOWN_CHECKS}""",
            json
        )
    }

    @Test
    fun `when services are up then performSelfTestAndReportAsJson returns JSON without error message`() {
        arrangeUp()

        val json = selfTest.performSelfTestAndReportAsJson()

        assertEquals(
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":0,"checks":$UP_CHECKS}""",
            json
        )
    }

    private fun arrangeUp() {
        arrangeStatus(ServiceStatus.UP)
    }

    private fun arrangeDown() {
        arrangeStatus(ServiceStatus.DOWN)
    }

    private fun arrangeStatus(status: ServiceStatus) {
        `when`(fssGatewayClient.ping())
            .thenReturn(PingResult(EgressService.FSS_GATEWAY, status, "fssgw-endpoint", "fssgw-message"))

        `when`(grunnbeloepClient.ping())
            .thenReturn(PingResult(EgressService.PENSJON_REGLER, status, "regler-endpoint", "regler-message"))

        `when`(idPortenClient.ping())
            .thenReturn(PingResult(EgressService.ID_PORTEN, status, "idporten-endpoint", "idporten-message"))

        `when`(entraIdClient.ping())
            .thenReturn(PingResult(EgressService.MICROSOFT_ENTRA_ID, status, "entraid-endpoint", "entraid-message"))

        `when`(opptjeningClient.ping())
            .thenReturn(PingResult(EgressService.PENSJONSOPPTJENING, status, "popp-endpoint", "popp-message"))

        `when`(penClient.ping())
            .thenReturn(PingResult(EgressService.PENSJONSFAGLIG_KJERNE, status, "pen-endpoint", "pen-message"))

        `when`(personClient.ping())
            .thenReturn(PingResult(EgressService.PERSONDATALOESNINGEN, status, "pdl-endpoint", "pdl-message"))

        `when`(skjermingClient.ping())
            .thenReturn(PingResult(EgressService.SKJERMEDE_PERSONER, status, "nom-endpoint", "nom-message"))

        `when`(tjenestepensjonClient.ping())
            .thenReturn(PingResult(EgressService.TJENESTEPENSJON, status, "tp-endpoint", "tp-message"))
    }

    private class TestClass(
        fssGatewayClient: FssGatewayPingClient,
        grunnbeloepClient: PensjonReglerGrunnbeloepClient,
        idPortenClient: IdPortenPingClient,
        entraIdOAuth2MetadataClient: AzureAdOAuth2MetadataClient,
        opptjeningClient: PoppOpptjeningsgrunnlagClient,
        penClient: PenPingClient,
        personClient: PdlPersonClient,
        skjermingClient: NomSkjermingClient,
        tjenestepensjonClient: TpTjenestepensjonClient
    ) :
        SelfTest(
            fssGatewayClient,
            grunnbeloepClient,
            idPortenClient,
            entraIdOAuth2MetadataClient,
            opptjeningClient,
            penClient,
            personClient,
            skjermingClient,
            tjenestepensjonClient
        ) {

        override fun now(): LocalTime {
            return LocalTime.of(12, 13, 14)
        }
    }

    private companion object {
        @Language("json")
        private const val UP_CHECKS: String = "[" +
                "{\"endpoint\":\"fssgw-endpoint\",\"description\":\"Fagsystemsone-gateway\",\"result\":0}, " +
                "{\"endpoint\":\"regler-endpoint\",\"description\":\"Pensjon-regler\",\"result\":0}, " +
                "{\"endpoint\":\"idporten-endpoint\",\"description\":\"ID-porten\",\"result\":0}, " +
                "{\"endpoint\":\"entraid-endpoint\",\"description\":\"Microsoft Entra ID\",\"result\":0}, " +
                "{\"endpoint\":\"popp-endpoint\",\"description\":\"Pensjonsopptjening\",\"result\":0}, " +
                "{\"endpoint\":\"pen-endpoint\",\"description\":\"Pensjonsfaglig kjerne\",\"result\":0}, " +
                "{\"endpoint\":\"pdl-endpoint\",\"description\":\"Persondataløsningen\",\"result\":0}, " +
                "{\"endpoint\":\"nom-endpoint\",\"description\":\"Skjermede personer\",\"result\":0}, " +
                "{\"endpoint\":\"tp-endpoint\",\"description\":\"Tjenestepensjon\",\"result\":0}" +
                "]"

        @Language("json")
        private const val DOWN_CHECKS: String = "[" +
                "{\"endpoint\":\"fssgw-endpoint\",\"description\":\"Fagsystemsone-gateway\",\"errorMessage\":\"fssgw-message\",\"result\":1}, " +
                "{\"endpoint\":\"regler-endpoint\",\"description\":\"Pensjon-regler\",\"errorMessage\":\"regler-message\",\"result\":1}, " +
                "{\"endpoint\":\"idporten-endpoint\",\"description\":\"ID-porten\",\"errorMessage\":\"idporten-message\",\"result\":1}, " +
                "{\"endpoint\":\"entraid-endpoint\",\"description\":\"Microsoft Entra ID\",\"errorMessage\":\"entraid-message\",\"result\":1}, " +
                "{\"endpoint\":\"popp-endpoint\",\"description\":\"Pensjonsopptjening\",\"errorMessage\":\"popp-message\",\"result\":1}, " +
                "{\"endpoint\":\"pen-endpoint\",\"description\":\"Pensjonsfaglig kjerne\",\"errorMessage\":\"pen-message\",\"result\":1}, " +
                "{\"endpoint\":\"pdl-endpoint\",\"description\":\"Persondataløsningen\",\"errorMessage\":\"pdl-message\",\"result\":1}, " +
                "{\"endpoint\":\"nom-endpoint\",\"description\":\"Skjermede personer\",\"errorMessage\":\"nom-message\",\"result\":1}, " +
                "{\"endpoint\":\"tp-endpoint\",\"description\":\"Tjenestepensjon\",\"errorMessage\":\"tp-message\",\"result\":1}" +
                "]"

        @Language("html")
        private const val TABLE_BODY: String = "<tbody>" +
                "<tr><td>Fagsystemsone-gateway</td><td style=\"background-color:green;text-align:center;\">UP</td><td>fssgw-message</td><td>fssgw-endpoint</td><td>Tilgang til Fagsystemsonen</td></tr>" +
                "<tr><td>Pensjon-regler</td><td style=\"background-color:green;text-align:center;\">UP</td><td>regler-message</td><td>regler-endpoint</td><td>Pensjonsregler</td></tr>" +
                "<tr><td>ID-porten</td><td style=\"background-color:green;text-align:center;\">UP</td><td>idporten-message</td><td>idporten-endpoint</td><td>Token-utsteder</td></tr>" +
                "<tr><td>Microsoft Entra ID</td><td style=\"background-color:green;text-align:center;\">UP</td><td>entraid-message</td><td>entraid-endpoint</td><td>OAuth2 configuration data</td></tr>" +
                "<tr><td>Pensjonsopptjening</td><td style=\"background-color:green;text-align:center;\">UP</td><td>popp-message</td><td>popp-endpoint</td><td>Pensjonsopptjeningsdata</td></tr>" +
                "<tr><td>Pensjonsfaglig kjerne</td><td style=\"background-color:green;text-align:center;\">UP</td><td>pen-message</td><td>pen-endpoint</td><td>Simulering, pensjonsdata</td></tr>" +
                "<tr><td>Persondataløsningen</td><td style=\"background-color:green;text-align:center;\">UP</td><td>pdl-message</td><td>pdl-endpoint</td><td>Persondata</td></tr>" +
                "<tr><td>Skjermede personer</td><td style=\"background-color:green;text-align:center;\">UP</td><td>nom-message</td><td>nom-endpoint</td><td>Skjerming</td></tr>" +
                "<tr><td>Tjenestepensjon</td><td style=\"background-color:green;text-align:center;\">UP</td><td>tp-message</td><td>tp-endpoint</td><td>Tjenestepensjonsforhold</td></tr>" +
                "</tbody>"
    }
}
