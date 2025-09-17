package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.client.PingableServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjonsforhold
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjon
import no.nav.pensjon.kalkulator.tjenestepensjon.FinnTjenestepensjonsforholdResponsDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpApotekerDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpTjenestepensjonStatusDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.TpTjenestepensjonDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map.TpTjenestepensjonMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.DefaultUriBuilderFactory
import java.time.LocalDate

/**
 * Client for accessing the 'tp' service (see https://github.com/navikt/tp)
 */
@Component
class TpTjenestepensjonClient(
    @Value("\${tjenestepensjon.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : PingableServiceClient(baseUrl, webClientBuilder, retryAttempts),
    TjenestepensjonClient {

    private val log = KotlinLogging.logger {}

    override fun pingPath(): String = PING_PATH

    override fun service(): EgressService = service

    override fun erApoteker(pid: Pid): Boolean {
        val url = "$baseUrl/$APOTEKER_PATH"
        log.debug { "GET from URL: '$url'" }

        return try {
            webClient
                .get()
                .uri("/$APOTEKER_PATH")
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(TpApotekerDto::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(TpTjenestepensjonMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: false
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: EgressException) {
            handle(e, pid) ?: throw e
        }
    }

    override fun harTjenestepensjonsforhold(pid: Pid, dato: LocalDate): Boolean {
        val uri = ytelseUri(dato)
        val url = "$baseUrl$uri"
        log.debug { "GET from URL: '$url'" }

        return try {
            webClient
                .get()
                .uri(uri)
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(TpTjenestepensjonStatusDto::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(TpTjenestepensjonMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: false
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun tjenestepensjon(pid: Pid): Tjenestepensjon {
        val url = "$baseUrl/$API_PATH/"
        log.debug { "GET from URL: '$url'" }

        return try {
            webClient
                .get()
                .uri("/$API_PATH/")
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(TpTjenestepensjonDto::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(TpTjenestepensjonMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: Tjenestepensjon(emptyList())
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun tjenestepensjonsforhold(pid: Pid): Tjenestepensjonsforhold {
        val url = "$baseUrl/$API_TP_FORHOLD/"
        log.debug { "GET from URL: '$url'" }

        return try {
            webClient
                .get()
                .uri("/$API_TP_FORHOLD/")
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(FinnTjenestepensjonsforholdResponsDto::class.java)
                .retryWhen(retryBackoffSpec(url))
                .block()
                ?.let(TpTjenestepensjonMapper::fromDto)
                .also { countCalls(MetricResult.OK) }
                ?: Tjenestepensjonsforhold(emptyList())
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $url", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun setPingHeaders(headers: HttpHeaders) {
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun ytelseUri(date: LocalDate) =
        DefaultUriBuilderFactory()
            .uriString("/$YTELSE_PATH")
            .queryParam("date", date.toString())
            .queryParam("ytelseType", TpYtelseType.ALDERSPENSJON.externalValue)
            .queryParam("ordningType", TpOrdningType.OFFENTLIG_TJENESTEPENSJONSORDNING.externalValue)
            .build()
            .toString()

    private fun setHeaders(headers: HttpHeaders, pid: Pid) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()

        // https://github.com/navikt/tp/blob/main/tp-api/src/main/kotlin/no/nav/samhandling/tp/provider/Headers.kt
        headers[CustomHttpHeaders.PID] = pid.value
    }

    private fun handle(e: EgressException, pid: Pid): Boolean? =
        e.message?.let {
            if (e.statusCode == HttpStatus.NOT_FOUND && it.contains("Person ikke funnet"))
                false.also { log.warn("Person ikke funnet - $pid") }
            else
                null
        }

    companion object {
        private const val API_PATH = "api/tjenestepensjon"
        private const val API_TP_FORHOLD = "api/tjenestepensjon/finnTjenestepensjonsforhold"
        private const val PING_PATH = "actuator/health/liveness"
        private const val APOTEKER_RESOURCE = "medlem/afp/apotekerforeningen/ersisteforhold"
        private const val APOTEKER_PATH = "$API_PATH/$APOTEKER_RESOURCE"

        // https://github.com/navikt/tp/blob/main/tp-api/src/main/kotlin/no/nav/samhandling/tp/controller/TjenestepensjonController.kt
        private const val YTELSE_RESOURCE = "haveYtelse"
        private const val YTELSE_PATH = "$API_PATH/$YTELSE_RESOURCE"

        private val service = EgressService.TJENESTEPENSJON
    }
}