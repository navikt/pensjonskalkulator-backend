package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.selftest.PingResult
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.selftest.ServiceStatus
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

abstract class PingableServiceClient(
    private val baseUrl: String,
    private val webClient: WebClient,
    private val traceAid: TraceAid,
    retryAttempts: String
) : ExternalServiceClient(retryAttempts), Pingable {

    abstract fun pingPath(): String

    abstract fun setPingHeaders(headers: HttpHeaders)

    override fun ping(): PingResult {
        val uri = "$baseUrl/${pingPath()}"

        return try {
            val responseBody = webClient
                .get()
                .uri(uri)
                .headers { setPingHeaders(it) }
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?: ""

            PingResult(service(), ServiceStatus.UP, uri, responseBody)
        } catch (e: EgressException) {
            // Happens if failing to obtain access token
            down(uri, e)
        } catch (e: WebClientRequestException) {
            down(uri, e)
        } catch (e: WebClientResponseException) {
            down(uri, e.responseBodyAsString)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders, pid: Pid? = null) {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(EgressAccess.token(service()).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        pid?.let { headers[CustomHttpHeaders.PID] = it.value }
    }

    private fun down(uri: String, e: Throwable) = down(uri, e.message ?: "Failed calling ${service()}")

    private fun down(uri: String, message: String) = PingResult(service(), ServiceStatus.DOWN, uri, message)
}
