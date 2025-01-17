package no.nav.pensjon.kalkulator.simulering.client.pen

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.PingableServiceClient
import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.api.dto.AnonymSimuleringErrorV1
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringError
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringResult
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenSimuleringResultDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringEgressSpecDto
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenAnonymSimuleringErrorMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenAnonymSimuleringResultMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenAnonymSimuleringSpecMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringResultMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringSpecMapper
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
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
import reactor.core.publisher.Mono

@Component
class PensjonssimulatorClient(
    @Value("\${pensjonssimulator.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PingableServiceClient(baseUrl, webClientBuilder, retryAttempts), SimuleringClient {

    override fun simulerAlderspensjon(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        doPost(
            path = PATH,
            requestBody = PenSimuleringSpecMapper.toDto(impersonalSpec, personalSpec),
            requestClass = SimuleringEgressSpecDto::class.java,
            responseClass = PenSimuleringResultDto::class.java
        )?.let(PenSimuleringResultMapper::fromDto)
            ?: emptyResult()



    override fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult {
        return try {
            doPost(
                path = ANONYM_PATH,
                requestBody = PenAnonymSimuleringSpecMapper.toDto(spec),
                requestClass = PenAnonymSimuleringSpec::class.java,
                responseClass = PenAnonymSimuleringResult::class.java,
            )?.let(PenAnonymSimuleringResultMapper::fromDto)
                ?: emptyResult()

        }  catch (e: EgressException) { //Adding formatted errorObject to the EgressException
            addFormattedErrorObjectToException(e)
            throw e
        }
    }

    private fun <Request : Any, Response> doPost(
        path: String,
        requestBody: Request,
        requestClass: Class<Request>,
        responseClass: Class<Response>,
    ): Response? {
        log.debug { "POST to URI: '$path'" }

        try {
            return webClient
                .post()
                .uri(path)
                .headers(::setHeaders)
                .body(Mono.just(requestBody), requestClass)
                .retrieve()
                .bodyToMono(responseClass)
                .retryWhen(retryBackoffSpec(path))
                .block()
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling ${service()}", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    fun setHeaders(headers: HttpHeaders, pid: Pid? = null) {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(EgressAccess.token(service()).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        pid?.let { headers[CustomHttpHeaders.PID] = it.value }
    }

    override fun pingPath() = PING_PATH

    override fun setPingHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service()).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun service() = EgressService.PENSJONSSIMULATOR

    private companion object {
        private const val PATH = "api/nav/v3/simuler-alderspensjon"
        private const val ANONYM_PATH = "api/anonym/v1/simuler-alderspensjon"
        private const val PING_PATH = "/ping"
        private val mapper = jacksonObjectMapper()
        private val log = KotlinLogging.logger {}

        private fun addFormattedErrorObjectToException(e: EgressException) {
            try {
                val jsonMap: PenAnonymSimuleringError = mapper.readValue(e.message, PenAnonymSimuleringError::class.java)
                e.errorObj = jsonMap.let(PenAnonymSimuleringErrorMapper::fromDto)
            } catch (_: JsonProcessingException) {
                log.debug { "Json mapping feilet. Ugjenkjennelig format på feilmelding fra PEN: ${e.message}"}
                e.errorObj =  AnonymSimuleringErrorV1(
                    status = "PEK500InternalServerError",
                    message = "Det skjedde en feil i kalkuleringen"
                )
            }
        }

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
