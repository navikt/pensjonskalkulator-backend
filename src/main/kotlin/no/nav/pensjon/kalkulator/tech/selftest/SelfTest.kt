package no.nav.pensjon.kalkulator.tech.selftest

import no.nav.pensjon.kalkulator.common.client.fssgw.FssGatewayPingClient
import no.nav.pensjon.kalkulator.common.client.pen.PenPingClient
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.PensjonReglerGrunnbeloepClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.PoppOpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.client.pdl.PdlPersonClient
import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdOAuth2MetadataClient
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.nom.NomSkjermingClient
import no.nav.pensjon.kalkulator.tech.security.ingress.ping.IdPortenPingClient
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClient
import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class SelfTest(
    fssGatewayClient: FssGatewayPingClient,
    grunnbeloepClient: PensjonReglerGrunnbeloepClient,
    idPortenClient: IdPortenPingClient,
    entraIdClient: AzureAdOAuth2MetadataClient,
    opptjeningClient: PoppOpptjeningsgrunnlagClient,
    penClient: PenPingClient,
    personClient: PdlPersonClient,
    skjermingClient: NomSkjermingClient,
    tjenestepensjonClient: TpTjenestepensjonClient
) {

    private val services: List<Pingable> = listOf(
        fssGatewayClient,
        grunnbeloepClient,
        idPortenClient,
        entraIdClient,
        opptjeningClient,
        penClient,
        personClient,
        skjermingClient,
        tjenestepensjonClient)

    /**
     * Returns result of self-test in HTML format.
     */
    fun performSelfTestAndReportAsHtml(): String = htmlPage(htmlStatusRows(performSelfTest()))

    /**
     * Returns result of self-test in JSON format.
     */
    fun performSelfTestAndReportAsJson(): String {
        val pingResults = performSelfTest()
        val aggregateCode = getAggregateResult(pingResults.values).code
        val checks = json(pingResults)
        return """{"application":"$APPLICATION_NAME","timestamp":"${now()}","aggregateResult":$aggregateCode,"checks":[$checks]}"""
    }

    protected fun now(): LocalTime = LocalTime.now()

    private fun performSelfTest(): Map<String, PingResult> =
        services
            .map { it.ping() }
            .associateBy { it.service.name }

    private companion object {
        private const val APPLICATION_NAME = "pensjonskalkulator-backend"

        private fun getAggregateResult(pingResults: Collection<PingResult>) =
            pingResults
                .map { it.status }
                .firstOrNull { it == ServiceStatus.DOWN } ?: ServiceStatus.UP

        private fun json(resultsByService: Map<String, PingResult>) = resultsByService.values.joinToString { json(it) }

        private fun json(result: PingResult): String {
            val errorMessageEntry =
                if (result.status == ServiceStatus.DOWN) ""","errorMessage":"${result.message}"""" else ""

            return """{"endpoint":"${result.endpoint}","description":"${result.service.description}"$errorMessageEntry,"result":${result.status.code}}"""
        }

        private fun htmlPage(tableRows: String) = """
            <!DOCTYPE html>
            <html>
            <head>
            <title>$APPLICATION_NAME selvtest</title>
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
            <tbody>$tableRows</tbody>
            </table>
            </div>
            </body>
            </html>
            """.trimIndent()

        private fun htmlStatusRows(resultsByService: Map<String, PingResult>) =
            resultsByService.entries.joinToString(separator = "", transform = ::htmlRow)

        private fun htmlRow(entry: Map.Entry<String, PingResult>) = htmlRow(entry.value)

        private fun htmlRow(result: PingResult) =
            "<tr>${htmlCell(result.service.description)}${htmlStatusCell(result.status)}${htmlCell(result.message)}" +
                    "${htmlCell(result.endpoint)}${htmlCell(result.service.purpose)}</tr>"

        private fun htmlCell(content: String) = "<td>$content</td>"

        private fun htmlStatusCell(status: ServiceStatus) =
            """<td style="background-color:${status.color};text-align:center;">${status.name}</td>"""
    }
}
