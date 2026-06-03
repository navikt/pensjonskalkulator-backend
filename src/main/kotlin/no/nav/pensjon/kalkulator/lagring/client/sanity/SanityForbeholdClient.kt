package no.nav.pensjon.kalkulator.lagring.client.sanity

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.lagring.ForbeholdInnhold
import no.nav.pensjon.kalkulator.lagring.client.sanity.dto.SanityQueryResponseDto
import no.nav.pensjon.kalkulator.lagring.client.sanity.map.SanityForbeholdMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Component
class SanityForbeholdClient(
    webClientBuilder: WebClient.Builder,
) : ForbeholdClient {

    private val log = KotlinLogging.logger {}

    private val baseUrl = "https://g2by7q6m.apicdn.sanity.io/v2025-07-02/data/query/development"

    private val webClient = webClientBuilder.build()

    override fun fetchForbehold(): ForbeholdInnhold? {
        val query = """*[_type == "forbeholdAvsnitt" && language == "nb" && visIntern == true] | order(order asc) | {_id,overskrift,"innhold":innholdIntern,alltidSynlig,vilkaar}"""

        log.debug { "GET Sanity query for forbehold" }

        val uri: URI = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("query", query)
            .queryParam("returnQuery", "false")
            .build()
            .encode()
            .toUri()

        val response = webClient
            .get()
            .uri(uri)
            .headers { headers ->
                headers.accept = listOf(MediaType.APPLICATION_JSON)
            }
            .retrieve()
            .bodyToMono<SanityQueryResponseDto>()
            .block()

        val documents = response?.result ?: return null
        return if (documents.isEmpty()) null else SanityForbeholdMapper.fromDto(documents)
    }
}
