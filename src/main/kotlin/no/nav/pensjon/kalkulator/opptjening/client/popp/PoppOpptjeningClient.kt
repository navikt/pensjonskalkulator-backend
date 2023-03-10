package no.nav.pensjon.kalkulator.opptjening.client.popp

import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.map.OpptjeningsgrunnlagMapper
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Component
class PoppOpptjeningClient(
    @Value("\${popp.url}") private val baseUrl: String,
    private val webClient: WebClient
) : OpptjeningsgrunnlagClient, Pingable {
    private val log = LogFactory.getLog(javaClass)

    /**
     * Calls PROPOPP007
     */
    override fun getOpptjeningsgrunnlag(pid: Pid): Opptjeningsgrunnlag {
        val uri = "$baseUrl$OPPTJENINGSGRUNNLAG_PATH/${pid.value}"

        if (log.isDebugEnabled) {
            log.debug("POST to URI: '${displayableUri(pid)}'")
        }

        try {
            val response = webClient
                .get()
                .uri(uri)
                .headers { setHeaders(it) }
                .retrieve()
                .bodyToMono(OpptjeningsgrunnlagResponseDto::class.java)
                .block()
                ?: emptyDto()

            return OpptjeningsgrunnlagMapper.fromDto(response)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to GET ${displayableUri(pid)}: ${e.message}", e)
        }
    }

    override fun ping(): PingResult {
        val uri = baseUrl + PING_PATH

        try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .headers { setPingHeaders(it) }
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""

            return PingResult(service, ServiceStatus.UP, uri, responseBody)
        } catch (e: WebClientResponseException) {
            return PingResult(service, ServiceStatus.DOWN, uri, e.responseBodyAsString)
        } catch (e: RuntimeException) { // e.g. when connection broken
            return PingResult(service, ServiceStatus.DOWN, uri, e.message ?: "Ping failed")
        }
    }

    private fun displayableUri(pid: Pid) = "$baseUrl$OPPTJENINGSGRUNNLAG_PATH/${pid.displayValue}"

    companion object {
        private const val OPPTJENINGSGRUNNLAG_PATH = "/api/opptjeningsgrunnlag"
        private const val PING_PATH = "/api/ping"
        private val service = EgressService.PENSJONSOPPTJENING

        private fun setHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun setPingHeaders(headers: HttpHeaders) {
            headers.setBearerAuth(EgressAccess.token(service).value)
            headers[CustomHttpHeaders.CALL_ID] = callId()
        }

        private fun callId() = UUID.randomUUID().toString()

        private fun emptyDto() = OpptjeningsgrunnlagResponseDto(OpptjeningsgrunnlagDto(emptyList()))
    }
}
