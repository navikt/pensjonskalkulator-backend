package no.nav.pensjon.kalkulator.simulering.client.simulator

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAnonymSimuleringResultEnvelope
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorPersonligSimuleringResult
import no.nav.pensjon.kalkulator.simulering.client.simulator.map.SimulatorAnonymSimuleringResultMapper
import no.nav.pensjon.kalkulator.simulering.client.simulator.map.SimulatorAnonymSimuleringSpecMapper
import no.nav.pensjon.kalkulator.simulering.client.simulator.map.SimulatorPersonligSimuleringResultMapper
import no.nav.pensjon.kalkulator.simulering.client.simulator.map.SimulatorPersonligSimuleringSpecMapper
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class PensjonssimulatorSimuleringClient(
    @Value("\${pensjonssimulator.url}") val baseUrl: String,
    val webClientBuilder: WebClient.Builder,
    val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : ExternalServiceClient(retryAttempts), SimuleringClient, Pingable {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()
    private val log = KotlinLogging.logger {}

    override fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult {
        val url = "$baseUrl/$SIMULER_ALDERSPENSJON_ANONYM_RESOURCE"
        log.debug { "POST to URL: '$url'" }

        return try {
            webClient
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(SimulatorAnonymSimuleringSpecMapper.toDto(spec))
                .retrieve()
                .bodyToMono(SimulatorAnonymSimuleringResultEnvelope::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(SimulatorAnonymSimuleringResultMapper::fromDto)
                ?: emptyResult()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun simulerPersonligAlderspensjon(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ): SimuleringResult {
        val url = "$baseUrl/$SIMULER_ALDERSPENSJON_PERSONLIG_RESOURCE"
        val spec = SimulatorPersonligSimuleringSpecMapper.toDto(impersonalSpec, personalSpec)

        return try {
            val result: SimulatorPersonligSimuleringResult? =
                webClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(::setHeaders)
                    .bodyValue(spec)
                    .retrieve()
                    .bodyToMono(SimulatorPersonligSimuleringResult::class.java)
                    .retryWhen(retryBackoffSpec(url))
                    .block()

            result?.error?.let { log.warn { "Error from simulator - $it - spec $spec" } }
            result?.let(SimulatorPersonligSimuleringResultMapper::fromDto) ?: emptyResult()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    override fun service(): EgressService = service

    override fun ping(): PingResult {
        TODO("Not yet implemented")
    }

    private companion object {
        private const val SIMULER_ALDERSPENSJON_ANONYM_RESOURCE = "api/anonym/v1/simuler-alderspensjon"
        private const val SIMULER_ALDERSPENSJON_PERSONLIG_RESOURCE = "api/nav/v3/simuler-alderspensjon"
        private val service = EgressService.PENSJONSSIMULATOR

        private fun emptyResult() =
            SimuleringResult(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList()
            )
    }
}
