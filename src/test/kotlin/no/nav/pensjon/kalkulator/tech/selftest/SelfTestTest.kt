package no.nav.pensjon.kalkulator.tech.selftest

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.common.client.fssgw.FssGatewayPingClient
import no.nav.pensjon.kalkulator.common.client.pen.PenPingClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.PoppOpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.client.pdl.PdlPersonClient
import no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.PensjonRepresentasjonClient
import no.nav.pensjon.kalkulator.tech.security.egress.azuread.AzureAdOAuth2MetadataClient
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.nom.NomSkjermingClient
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClient
import org.intellij.lang.annotations.Language
import java.time.LocalTime

class SelfTestTest : ShouldSpec({

    context("performSelfTestAndReportAsHtml when services are up") {
        should("return HTML indicating up") {
            selfTest(status = ServiceStatus.UP).performSelfTestAndReportAsHtml() shouldBe
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
</html>"""
        }
    }

    context("performSelfTestAndReportAsJson when services are up") {
        should("return JSON without error message") {
            selfTest(status = ServiceStatus.UP).performSelfTestAndReportAsJson() shouldBe
                    """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":0,"checks":$UP_CHECKS}"""
        }
    }

    context("performSelfTestAndReportAsJson when services are down") {
        should("return JSON containing error message") {
            selfTest(status = ServiceStatus.DOWN).performSelfTestAndReportAsJson() shouldBe
                    """{"application":"pensjonskalkulator-backend","timestamp":"12:13:14","aggregateResult":1,"checks":$DOWN_CHECKS}"""
        }
    }
})

private fun selfTest(status: ServiceStatus) =
    TestClass(
        fssGatewayClient = arrangeFssGateway(status),
        entraIdOAuth2MetadataClient = arrangeEntraId(status),
        opptjeningClient = arrangePopp(status),
        penClient = arrangePen(status),
        personClient = arrangePdl(status),
        representasjonClient = arrangePensjonRepresentasjon(status),
        skjermingClient = arrangeNomSkjerming(status),
        tjenestepensjonClient = arrangeTp(status)
    )

private fun arrangeFssGateway(status: ServiceStatus): FssGatewayPingClient =
    mockk<FssGatewayPingClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.FSS_GATEWAY,
            status = status,
            endpoint = "fssgw-endpoint",
            message = "fssgw-message"
        )
    }

private fun arrangeEntraId(status: ServiceStatus): AzureAdOAuth2MetadataClient =
    mockk<AzureAdOAuth2MetadataClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.MICROSOFT_ENTRA_ID,
            status = status,
            endpoint = "entraid-endpoint",
            message = "entraid-message"
        )
    }

private fun arrangePopp(status: ServiceStatus): PoppOpptjeningsgrunnlagClient =
    mockk<PoppOpptjeningsgrunnlagClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.PENSJONSOPPTJENING,
            status = status,
            endpoint = "popp-endpoint",
            message = "popp-message"
        )
    }

private fun arrangePen(status: ServiceStatus): PenPingClient =
    mockk<PenPingClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.PENSJONSFAGLIG_KJERNE,
            status = status,
            endpoint = "pen-endpoint",
            message = "pen-message"
        )
    }

private fun arrangePdl(status: ServiceStatus): PdlPersonClient =
    mockk<PdlPersonClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.PERSONDATALOESNINGEN,
            status = status,
            endpoint = "pdl-endpoint",
            message = "pdl-message"
        )
    }

private fun arrangePensjonRepresentasjon(status: ServiceStatus): PensjonRepresentasjonClient =
    mockk<PensjonRepresentasjonClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.PENSJON_REPRESENTASJON,
            status = status,
            endpoint = "repr-endpoint",
            message = "repr-message"
        )
    }

private fun arrangeNomSkjerming(status: ServiceStatus): NomSkjermingClient =
    mockk<NomSkjermingClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.SKJERMEDE_PERSONER,
            status = status,
            endpoint = "nom-endpoint",
            message = "nom-message"
        )
    }

private fun arrangeTp(status: ServiceStatus): TpTjenestepensjonClient =
    mockk<TpTjenestepensjonClient>().apply {
        every { ping() } returns PingResult(
            service = EgressService.TJENESTEPENSJON,
            status = status,
            endpoint = "tp-endpoint",
            message = "tp-message"
        )
    }

private class TestClass(
    fssGatewayClient: FssGatewayPingClient,
    entraIdOAuth2MetadataClient: AzureAdOAuth2MetadataClient,
    opptjeningClient: PoppOpptjeningsgrunnlagClient,
    penClient: PenPingClient,
    personClient: PdlPersonClient,
    representasjonClient: PensjonRepresentasjonClient,
    skjermingClient: NomSkjermingClient,
    tjenestepensjonClient: TpTjenestepensjonClient
) :
    SelfTest(
        fssGatewayClient,
        entraIdOAuth2MetadataClient,
        opptjeningClient,
        penClient,
        personClient,
        representasjonClient,
        skjermingClient,
        tjenestepensjonClient
    ) {

    override fun now(): LocalTime =
        LocalTime.of(12, 13, 14)
}

@Language("json")
private const val UP_CHECKS: String = "[" +
        "{\"endpoint\":\"fssgw-endpoint\",\"description\":\"Fagsystemsone-gateway\",\"result\":0}, " +
        "{\"endpoint\":\"entraid-endpoint\",\"description\":\"Microsoft Entra ID\",\"result\":0}, " +
        "{\"endpoint\":\"popp-endpoint\",\"description\":\"Pensjonsopptjening\",\"result\":0}, " +
        "{\"endpoint\":\"pen-endpoint\",\"description\":\"Pensjonsfaglig kjerne\",\"result\":0}, " +
        "{\"endpoint\":\"pdl-endpoint\",\"description\":\"Persondataløsningen\",\"result\":0}, " +
        "{\"endpoint\":\"repr-endpoint\",\"description\":\"Pensjon-representasjon\",\"result\":0}, " +
        "{\"endpoint\":\"nom-endpoint\",\"description\":\"Skjermede personer\",\"result\":0}, " +
        "{\"endpoint\":\"tp-endpoint\",\"description\":\"Tjenestepensjon\",\"result\":0}" +
        "]"

@Language("json")
private const val DOWN_CHECKS: String = "[" +
        "{\"endpoint\":\"fssgw-endpoint\",\"description\":\"Fagsystemsone-gateway\",\"errorMessage\":\"fssgw-message\",\"result\":1}, " +
        "{\"endpoint\":\"entraid-endpoint\",\"description\":\"Microsoft Entra ID\",\"errorMessage\":\"entraid-message\",\"result\":1}, " +
        "{\"endpoint\":\"popp-endpoint\",\"description\":\"Pensjonsopptjening\",\"errorMessage\":\"popp-message\",\"result\":1}, " +
        "{\"endpoint\":\"pen-endpoint\",\"description\":\"Pensjonsfaglig kjerne\",\"errorMessage\":\"pen-message\",\"result\":1}, " +
        "{\"endpoint\":\"pdl-endpoint\",\"description\":\"Persondataløsningen\",\"errorMessage\":\"pdl-message\",\"result\":1}, " +
        "{\"endpoint\":\"repr-endpoint\",\"description\":\"Pensjon-representasjon\",\"errorMessage\":\"repr-message\",\"result\":1}, " +
        "{\"endpoint\":\"nom-endpoint\",\"description\":\"Skjermede personer\",\"errorMessage\":\"nom-message\",\"result\":1}, " +
        "{\"endpoint\":\"tp-endpoint\",\"description\":\"Tjenestepensjon\",\"errorMessage\":\"tp-message\",\"result\":1}" +
        "]"

@Language("html")
private const val TABLE_BODY: String = "<tbody>" +
        "<tr><td>Fagsystemsone-gateway</td><td style=\"background-color:green;text-align:center;\">UP</td><td>fssgw-message</td><td>fssgw-endpoint</td><td>Tilgang til Fagsystemsonen</td></tr>" +
        "<tr><td>Microsoft Entra ID</td><td style=\"background-color:green;text-align:center;\">UP</td><td>entraid-message</td><td>entraid-endpoint</td><td>OAuth2 configuration data</td></tr>" +
        "<tr><td>Pensjonsopptjening</td><td style=\"background-color:green;text-align:center;\">UP</td><td>popp-message</td><td>popp-endpoint</td><td>Pensjonsopptjeningsdata</td></tr>" +
        "<tr><td>Pensjonsfaglig kjerne</td><td style=\"background-color:green;text-align:center;\">UP</td><td>pen-message</td><td>pen-endpoint</td><td>Simulering, pensjonsdata</td></tr>" +
        "<tr><td>Persondataløsningen</td><td style=\"background-color:green;text-align:center;\">UP</td><td>pdl-message</td><td>pdl-endpoint</td><td>Persondata</td></tr>" +
        "<tr><td>Pensjon-representasjon</td><td style=\"background-color:green;text-align:center;\">UP</td><td>repr-message</td><td>repr-endpoint</td><td>Representasjonsforhold (fullmakt m.m.)</td></tr>" +
        "<tr><td>Skjermede personer</td><td style=\"background-color:green;text-align:center;\">UP</td><td>nom-message</td><td>nom-endpoint</td><td>Skjerming</td></tr>" +
        "<tr><td>Tjenestepensjon</td><td style=\"background-color:green;text-align:center;\">UP</td><td>tp-message</td><td>tp-endpoint</td><td>Tjenestepensjonsforhold</td></tr>" +
        "</tbody>"
