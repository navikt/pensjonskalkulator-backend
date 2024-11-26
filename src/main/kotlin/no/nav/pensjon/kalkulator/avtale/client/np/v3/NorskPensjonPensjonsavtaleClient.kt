package no.nav.pensjon.kalkulator.avtale.client.np.v3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleException
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.NorskPensjonPensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.NorskPensjonUttaksperiodeSpecDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.map.NorskPensjonPensjonsavtaleMapper
import no.nav.pensjon.kalkulator.avtale.client.np.v3.map.NorskPensjonPensjonsavtaleMapper.fromDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.metric.NorskPensjonPensjonsavtaleMetrics.updateMetrics
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.GatewayUsage
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.nio.charset.StandardCharsets

/**
 * Denne klienten skal hente pensjonsavtaler fra Norsk pensjon i prod og dev
 */
@Component("norskPensjon")
class NorskPensjonPensjonsavtaleClient(
    @Value("\${norsk-pensjon.url}") private val baseUrl: String,
    private val tokenGetter: SamlTokenService,
    webClientBuilder: WebClient.Builder,
    private val xmlMapper: XmlMapper,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : ExternalServiceClient(retryAttempts), PensjonsavtaleClient {

    val webClient: WebClient = webClientBuilder
        .baseUrl(baseUrl)
        .defaultHeaders { it.contentType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8) }
        .build()

    private val log = KotlinLogging.logger {}

    override fun service() = service

    override fun fetchAvtaler(spec: PensjonsavtaleSpec, pid: Pid): Pensjonsavtaler {
        val responseXml = fetchAvtalerXml(NorskPensjonPensjonsavtaleMapper.toDto(spec, pid))
        countCalls(MetricResult.OK)

        return try {
            val dto = xmlMapper.readValue(responseXml, EnvelopeDto::class.java).also(::updateMetrics)
            fromDto(dto)
        } catch (e: JsonProcessingException) {
            log.error(e) { "Failed to process XML: $responseXml" }
            countCalls(MetricResult.BAD_XML)
            ingenAvtaler()
        } catch (e: PensjonsavtaleException) {
            log.warn(e) { "Pensjonsavtaler respons fault - ${e.message}" }
            ingenAvtaler()
        }
    }

    protected fun fetchAvtalerXml(spec: NorskPensjonPensjonsavtaleSpecDto): String {
        val uri = "/$RESOURCE"
        val callId = traceAid.callId()
        val body = soapEnvelope(soapBody(spec, callId))

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { setHeaders(it, callId) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $baseUrl$uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    protected fun soapEnvelope(body: String) =
        """<?xml version="1.0" ?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:typ="http://norskpensjon.no/api/pensjonskalkulator/v3/typer">
    ${soapHeader()}
    $body
</S:Envelope>"""

    protected fun soapHeader() =
        """<S:Header>
        ${tokenGetter.assertion()}
    </S:Header>"""

    private fun setHeaders(headers: HttpHeaders, callId: String) {
        if (service.gatewayUsage == GatewayUsage.INTERNAL) {
            headers.setBearerAuth(EgressAccess.token(service).value)
        }

        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_XML_VALUE
        headers[CustomHttpHeaders.CALL_ID] = callId
    }

    override fun toString(e: EgressException, uri: String) =
        xmlMapper.readValue(
            e.message,
            EnvelopeDto::class.java
        ).body?.fault?.let(NorskPensjonPensjonsavtaleMapper::faultToString) ?: e.message ?: "Failed to call $baseUrl$uri"

    companion object {
        private const val RESOURCE = "kalkulator.pensjonsrettighetstjeneste/v3/kalkulatorPensjonTjeneste"
        private const val ORGANISASJONSNUMMER = "889640782" // ARBEIDS- OG VELFERDSETATEN

        private val service = EgressService.NORSK_PENSJON

        @Language("xml")
        fun soapBody(spec: NorskPensjonPensjonsavtaleSpecDto, callId: String): String =
            """<S:Body>
        ${xml(spec, callId)}
    </S:Body>"""

        // NB: Order of XML elements is important (otherwise response may be 500)
        private fun xml(spec: NorskPensjonPensjonsavtaleSpecDto, callId: String): String =
            """<typ:kalkulatorForespoersel>
            <userSessionCorrelationID>$callId</userSessionCorrelationID>
            <organisasjonsnummer>$ORGANISASJONSNUMMER</organisasjonsnummer>
            <rettighetshaver>
                <foedselsnummer>${spec.pid.value}</foedselsnummer>
                <aarligInntektFoerUttak>${spec.aarligInntektFoerUttak}</aarligInntektFoerUttak>
                ${xml(spec.uttaksperioder)}
                <antallInntektsaarEtterUttak>${spec.antallInntektsaarEtterUttak}</antallInntektsaarEtterUttak>
                <harAfp>${spec.harAfp}</harAfp>
                <antallAarIUtlandetEtter16>${spec.antallAarIUtlandetEtter16}</antallAarIUtlandetEtter16>
                <sivilstatus>${spec.sivilstatus.externalValue}</sivilstatus>
                <harEpsPensjon>${spec.harEpsPensjon}</harEpsPensjon>
                <harEpsPensjonsgivendeInntektOver2G>${spec.harEpsPensjonsgivendeInntektOver2G}</harEpsPensjonsgivendeInntektOver2G>
                <oenskesSimuleringAvFolketrygd>${spec.oenskesSimuleringAvFolketrygd}</oenskesSimuleringAvFolketrygd>
            </rettighetshaver>
        </typ:kalkulatorForespoersel>"""

        private fun xml(specs: List<NorskPensjonUttaksperiodeSpecDto>): String =
            specs.joinToString(separator = "", transform = ::xml)

        @Language("xml")
        private fun xml(spec: NorskPensjonUttaksperiodeSpecDto): String =
            """<uttaksperiode>
                    <startAlder>${spec.startAlder.aar}</startAlder>
                    <startMaaned>${spec.startAlder.maaned}</startMaaned>
                    <grad>${spec.grad.prosentsats}</grad>
                    <aarligInntekt>${spec.aarligInntekt}</aarligInntekt>
                </uttaksperiode>"""

        private fun ingenAvtaler() = Pensjonsavtaler(avtaler = emptyList(), utilgjengeligeSelskap = emptyList())
    }
}
