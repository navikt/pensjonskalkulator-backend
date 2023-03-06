package no.nav.pensjon.kalkulator.tech.selftest

import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.PensjonReglerGrunnbeloepClient
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

    @BeforeEach
    fun initialize() {
        selfTest = TestClass(grunnbeloepClient)
    }

    @Test
    fun `when service is up then performSelfTestAndReportAsHtml returns HTML indicating up`() {
        `when`(grunnbeloepClient.ping())
            .thenReturn(PingResult(EgressService.PENSJON_REGLER, ServiceStatus.UP, "endpoint1", "message1"))

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
<tbody><tr><td>PENSJON_REGLER</td><td style="background-color:green;text-align:center;">UP</td><td>message1</td><td>endpoint1</td><td>Pensjonsregler</td></tr></tbody>
</table>
</div>
</body>
</html>""", html
        )
    }

    @Test
    fun `when service is down then performSelfTestAndReportAsJson returns JSON containing error message`() {
        `when`(grunnbeloepClient.ping())
            .thenReturn(PingResult(EgressService.PENSJON_REGLER, ServiceStatus.DOWN, "endpoint1", "message1"))

        val json = selfTest.performSelfTestAndReportAsJson()

        assertEquals(
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":1,"checks":[{"endpoint":"endpoint1","description":"Pensjonsregler","errorMessage":"message1","result":1}]}""",
            json
        )
    }

    @Test
    fun `when service is up then performSelfTestAndReportAsJson returns JSON without error message`() {
        `when`(grunnbeloepClient.ping())
            .thenReturn(PingResult(EgressService.PENSJON_REGLER, ServiceStatus.UP, "endpoint1", "message1"))

        val json = selfTest.performSelfTestAndReportAsJson()

        assertEquals(
            """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":0,"checks":[{"endpoint":"endpoint1","description":"Pensjonsregler","result":0}]}""",
            json
        )
    }

    private class TestClass(client: PensjonReglerGrunnbeloepClient) : SelfTest(client) {
        override fun now(): LocalTime {
            return LocalTime.of(12, 13, 14)
        }
    }
}
