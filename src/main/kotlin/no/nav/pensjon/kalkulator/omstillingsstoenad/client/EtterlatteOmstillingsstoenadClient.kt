package no.nav.pensjon.kalkulator.omstillingsstoenad.client

import kotlinx.coroutines.reactor.awaitSingle
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.omstillingsstoenad.client.dto.MottarOmstillingsstoenadDto
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.metric.MetricResult
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
import org.springframework.web.util.DefaultUriBuilderFactory
import java.time.LocalDate

@Component
class EtterlatteOmstillingsstoenadClient(
    @Value("\${omstillingsstoenad.url}") private val baseUrl: String,
    webClientBuilder: WebClient.Builder,
    private val traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : ExternalServiceClient(retryAttempts), OmstillingsstoenadClient {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    override suspend fun mottarOmstillingsstoenad(pid: Pid, paaDato: LocalDate): Boolean {
        val uri = uri(paaDato)

        return try {
            webClient
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .bodyValue(RequestBody(pid.value))
                .retrieve()
                .bodyToMono(MottarOmstillingsstoenadDto::class.java)
                .retryWhen(retryBackoffSpec(uri))
                .awaitSingle()
                ?.omstillingsstoenad
                .also { countCalls(MetricResult.OK) } == true
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    override fun service() = service

    private fun uri(dato: LocalDate) =
        DefaultUriBuilderFactory()
            .uriString("$baseUrl/$MOTTAR_OMSTILLINGSSTOENAD_PATH")
            .queryParam("paaDato", dato.toString())
            .build()
            .toString()

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    private data class RequestBody(val foedselsnummer: String)

    companion object {
        private const val MOTTAR_OMSTILLINGSSTOENAD_PATH = "api/pensjon/vedtak/har-loepende-oms"
        private val service = EgressService.OMSTILLINGSSTOENAD
    }
}
