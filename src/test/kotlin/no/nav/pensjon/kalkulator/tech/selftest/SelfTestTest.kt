package no.nav.pensjon.kalkulator.tech.selftest

import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.PensjonReglerGrunnbeloepClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.PoppOpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.client.pdl.PdlPersonClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
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
    private lateinit var personClient: PdlPersonClient

    @BeforeEach
    fun initialize() {
        selfTest = TestClass(grunnbeloepClient, opptjeningClient, personClient)
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
<tbody><tr><td>PENSJON_REGLER</td><td style="background-color:green;text-align:center;">UP</td><td>message1</td><td>endpoint1</td><td>Pensjon-regler</td></tr><tr><td>PENSJONSOPPTJENING</td><td style="background-color:green;text-align:center;">UP</td><td>message2</td><td>endpoint2</td><td>Pensjonsopptjening</td></tr><tr><td>PERSONDATALOESNINGEN</td><td style="background-color:green;text-align:center;">UP</td><td>message3</td><td>endpoint3</td><td>Persondataløsningen</td></tr></tbody>
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
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":1,"checks":[{"endpoint":"endpoint1","description":"Pensjon-regler","errorMessage":"message1","result":1}, {"endpoint":"endpoint2","description":"Pensjonsopptjening","errorMessage":"message2","result":1}, {"endpoint":"endpoint3","description":"Persondataløsningen","errorMessage":"message3","result":1}]}""",
            json
        )
    }

    @Test
    fun `when services are up then performSelfTestAndReportAsJson returns JSON without error message`() {
        arrangeUp()

        val json = selfTest.performSelfTestAndReportAsJson()

        assertEquals(
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":0,"checks":[{"endpoint":"endpoint1","description":"Pensjon-regler","result":0}, {"endpoint":"endpoint2","description":"Pensjonsopptjening","result":0}, {"endpoint":"endpoint3","description":"Persondataløsningen","result":0}]}""",
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

        `when`(personClient.ping())
            .thenReturn(PingResult(EgressService.PERSONDATALOESNINGEN, status, "endpoint3", "message3"))
    }

    private class TestClass(
        grunnbeloepClient: PensjonReglerGrunnbeloepClient,
        opptjeningClient: PoppOpptjeningsgrunnlagClient,
        personClient: PdlPersonClient
    ) :
        SelfTest(grunnbeloepClient, opptjeningClient, personClient) {
        override fun now(): LocalTime {
            return LocalTime.of(12, 13, 14)
        }
    }
}
