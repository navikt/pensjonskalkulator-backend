package no.nav.pensjon.kalkulator.grunnbeloep.regler

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pensjon.kalkulator.grunnbeloep.GrunnbeloepClient
import no.nav.pensjon.kalkulator.grunnbeloep.regler.dto.SatsResponse
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class PensjonReglerGrunnbeloepClient(
    @Value("\${pensjon-regler.url}") private val uri: String,
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) : GrunnbeloepClient {
    private val log = LogFactory.getLog(javaClass)

    override fun getGrunnbeloep(requestBody: String): SatsResponse {
        if (log.isDebugEnabled) {
            log.debug("POST to URI: '$uri'")
        }

        try {
            val responseBody = webClient
                .post()
                .uri(uri + PATH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: ""

            return objectMapper.readValue(responseBody, SatsResponse::class.java)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            throw EgressException("Failed to do POST towards $uri: ${e.message}", e)
        }
    }

    companion object {
        private const val PATH = "/api/hentGrunnbelopListe"
    }
}
