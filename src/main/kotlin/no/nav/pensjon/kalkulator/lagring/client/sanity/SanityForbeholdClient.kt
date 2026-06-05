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
    @Value("\${sanity.project-id}") private val projectId: String,
    @Value("\${sanity.dataset}") private val dataset: String,
    webClientBuilder: WebClient.Builder,
) : ForbeholdClient {

    private val log = KotlinLogging.logger {}

    private val sanityUrl = "https://$projectId.apicdn.sanity.io/v2025-07-02/data/query/$dataset"

    private val webClient = webClientBuilder.build()

    override fun fetchForbehold(): ForbeholdInnhold? {
        val query = """*[_type == "forbeholdAvsnitt" && language == "nb" && visIntern == true] | order(order asc) | {_id,overskrift,"innhold":innholdIntern,alltidSynlig,vilkaar}"""

        log.debug { "GET Sanity query for forbehold" }

        val uri: URI = UriComponentsBuilder.fromUriString(sanityUrl)
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
