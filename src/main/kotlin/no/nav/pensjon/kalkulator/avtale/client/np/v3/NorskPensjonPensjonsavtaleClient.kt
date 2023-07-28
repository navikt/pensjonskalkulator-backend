package no.nav.pensjon.kalkulator.avtale.client.np.v3

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.map.PensjonsavtaleMapper
import no.nav.pensjon.kalkulator.avtale.client.np.v3.map.PensjonsavtaleMapper.fromDto
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.config.GatewayUsage
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenService
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration
import java.util.*

@Component
class NorskPensjonPensjonsavtaleClient(
    @Value("\${norsk-pensjon.url}") private val baseUrl: String,
    private val tokenGetter: SamlTokenService,
    @Qualifier("soap") private val webClient: WebClient,
    private val xmlMapper: XmlMapper,
    private val callIdGenerator: CallIdGenerator
) : PensjonsavtaleClient {
    private val log = KotlinLogging.logger {}

    override fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        val responseXml = fetchAvtalerXml(spec)

        return try {
            val dto = xmlMapper.readValue(responseXml, EnvelopeDto::class.java)
            fromDto(dto)
        } catch (e: JsonProcessingException) {
            log.error(e) { "Failed to process JSON" }
            ingenAvtaler()
        }
    }

    private fun fetchAvtalerXml(spec: PensjonsavtaleSpec): String {
        val uri = "$baseUrl$PATH"
        val callId = callIdGenerator.newId()
        val body = soapEnvelope(soapBody(spec, callId))
        log.debug { "POST to URI: '$uri' with body '$body'" }

        try {
            return webClient
                .post()
                .uri(uri)
                .headers { setHeaders(it, callId) }
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(
                    Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(1))
                        .filter { it is EgressException && !it.isClientError }
                        .onRetryExhaustedThrow { backoff, signal -> handleFailure(backoff, signal) })
                .block()
                ?: ""
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    private fun soapEnvelope(body: String) =
        """<?xml version="1.0" ?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:typ="http://norskpensjon.no/api/pensjonskalkulator/v3/typer">
    ${soapHeader()}
    $body
</S:Envelope>"""

    private fun soapHeader() =
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

    private fun handleFailure(backoff: RetryBackoffSpec, retrySignal: Retry.RetrySignal): Throwable {
        log.info { "Retried calling $baseUrl$PATH ${backoff.maxAttempts} times" }

        return when (val failure = retrySignal.failure()) {
            is WebClientRequestException -> EgressException(true, "Failed calling ${failure.uri}", failure)
            is EgressException -> EgressException(failure.isClientError, toString(failure), failure)
            else -> failure
        }
    }

    private fun toString(e: EgressException) =
        xmlMapper.readValue(
            e.message,
            EnvelopeDto::class.java
        ).body?.fault?.let(PensjonsavtaleMapper::faultToString) ?: e.message ?: "Failed to call $baseUrl$PATH"

    companion object {
        private const val PATH = "/kalkulator.pensjonsrettighetstjeneste/v3/kalkulatorPensjonTjeneste"
        private const val ORGANISASJONSNUMMER = "889640782" // ARBEIDS- OG VELFERDSETATEN
        private const val RETRY_ATTEMPTS = 1L

        private val service = EgressService.PENSJONSAVTALER

        private fun soapBody(spec: PensjonsavtaleSpec, callId: String): String {
            return """<S:Body>
        ${xml(spec, callId)}
    </S:Body>"""
        }

        // NB: Order of XML elements is important (otherwise response may be 500)
        private fun xml(spec: PensjonsavtaleSpec, callId: String): String {
            return """<typ:kalkulatorForespoersel>
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
        }

        private fun xml(specs: List<UttaksperiodeSpec>): String {
            return specs.joinToString("", transform = ::xml)
        }

        private fun xml(spec: UttaksperiodeSpec): String {
            return """<uttaksperiode>
                    <startAlder>${spec.start.aar}</startAlder>
                    <startMaaned>${spec.start.maaned}</startMaaned>
                    <grad>${spec.grad.prosentsats}</grad>
                    <aarligInntekt>${spec.aarligInntekt}</aarligInntekt>
                </uttaksperiode>"""
        }

        private fun ingenAvtaler() = Pensjonsavtaler(emptyList(), emptyList())
    }
}
