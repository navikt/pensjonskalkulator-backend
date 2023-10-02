package no.nav.pensjon.kalkulator.tech.selftest

import no.nav.pensjon.kalkulator.common.client.pen.PenPingClient
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.PensjonReglerGrunnbeloepClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.PoppOpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.client.pdl.PdlPersonClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
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
    private lateinit var grunnbeloepClient: PensjonReglerGrunnbeloepClient

    @Mock
    private lateinit var opptjeningClient: PoppOpptjeningsgrunnlagClient

    @Mock
    private lateinit var penClient: PenPingClient

    @Mock
    private lateinit var personClient: PdlPersonClient

    @Mock
    private lateinit var tjenestepensjonClient: TpTjenestepensjonClient

    @BeforeEach
    fun initialize() {
        selfTest = TestClass(grunnbeloepClient, opptjeningClient, penClient, personClient, tjenestepensjonClient)
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
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":1,"checks":$ERROR_CHECKS}""",
            json
        )
    }

    @Test
    fun `when services are up then performSelfTestAndReportAsJson returns JSON without error message`() {
        arrangeUp()

        val json = selfTest.performSelfTestAndReportAsJson()

        assertEquals(
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":0,"checks":$OK_CHECKS}""",
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
        `when`(grunnbeloepClient.ping())
            .thenReturn(PingResult(EgressService.PENSJON_REGLER, status, "endpoint1", "message1"))

        `when`(opptjeningClient.ping())
            .thenReturn(PingResult(EgressService.PENSJONSOPPTJENING, status, "endpoint2", "message2"))

        `when`(penClient.ping())
            .thenReturn(PingResult(EgressService.PENSJONSFAGLIG_KJERNE, status, "endpoint5", "message5"))

        `when`(personClient.ping())
            .thenReturn(PingResult(EgressService.PERSONDATALOESNINGEN, status, "endpoint3", "message3"))

        `when`(tjenestepensjonClient.ping())
            .thenReturn(PingResult(EgressService.TJENESTEPENSJON, status, "endpoint4", "message4"))
    }

    private class TestClass(
        grunnbeloepClient: PensjonReglerGrunnbeloepClient,
        opptjeningClient: PoppOpptjeningsgrunnlagClient,
        penClient: PenPingClient,
        personClient: PdlPersonClient,
        tjenestepensjonClient: TpTjenestepensjonClient
    ) :
        SelfTest(grunnbeloepClient, opptjeningClient, penClient, personClient, tjenestepensjonClient) {

        override fun now(): LocalTime {
            return LocalTime.of(12, 13, 14)
        }
    }

    private companion object {
        @Language("json")
        private const val OK_CHECKS: String =
            "[" +
                    "{\"endpoint\":\"endpoint1\",\"description\":\"Pensjon-regler\",\"result\":0}," +
                    " {\"endpoint\":\"endpoint2\",\"description\":\"Pensjonsopptjening\",\"result\":0}," +
                    " {\"endpoint\":\"endpoint5\",\"description\":\"Pensjonsfaglig kjerne\",\"result\":0}," +
                    " {\"endpoint\":\"endpoint3\",\"description\":\"Persondataløsningen\",\"result\":0}," +
                    " {\"endpoint\":\"endpoint4\",\"description\":\"Tjenestepensjon\",\"result\":0}" +
                    "]"

        @Language("json")
        private const val ERROR_CHECKS: String =
            "[" +
                    "{\"endpoint\":\"endpoint1\",\"description\":\"Pensjon-regler\",\"errorMessage\":\"message1\",\"result\":1}," +
                    " {\"endpoint\":\"endpoint2\",\"description\":\"Pensjonsopptjening\",\"errorMessage\":\"message2\",\"result\":1}," +
                    " {\"endpoint\":\"endpoint5\",\"description\":\"Pensjonsfaglig kjerne\",\"errorMessage\":\"message5\",\"result\":1}," +
                    " {\"endpoint\":\"endpoint3\",\"description\":\"Persondataløsningen\",\"errorMessage\":\"message3\",\"result\":1}," +
                    " {\"endpoint\":\"endpoint4\",\"description\":\"Tjenestepensjon\",\"errorMessage\":\"message4\",\"result\":1}" +
                    "]"

        @Language("html")
        private const val TABLE_BODY: String =
            "<tbody>" +
                    "<tr><td>Pensjon-regler</td><td style=\"background-color:green;text-align:center;\">UP</td><td>message1</td><td>endpoint1</td><td>Pensjonsregler</td></tr>" +
                    "<tr><td>Pensjonsopptjening</td><td style=\"background-color:green;text-align:center;\">UP</td><td>message2</td><td>endpoint2</td><td>Pensjonsopptjeningsdata</td></tr>" +
                    "<tr><td>Pensjonsfaglig kjerne</td><td style=\"background-color:green;text-align:center;\">UP</td><td>message5</td><td>endpoint5</td><td>Simulering, pensjonsdata</td></tr>" +
                    "<tr><td>Persondataløsningen</td><td style=\"background-color:green;text-align:center;\">UP</td><td>message3</td><td>endpoint3</td><td>Persondata</td></tr>" +
                    "<tr><td>Tjenestepensjon</td><td style=\"background-color:green;text-align:center;\">UP</td><td>message4</td><td>endpoint4</td><td>Tjenestepensjonsforhold</td></tr>" +
                    "</tbody>"
    }
}
