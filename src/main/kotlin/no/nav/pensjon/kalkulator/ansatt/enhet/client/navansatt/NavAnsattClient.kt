package no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt

import com.github.benmanes.caffeine.cache.Cache
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.ansatt.enhet.AnsattEnhetResult
import no.nav.pensjon.kalkulator.ansatt.enhet.client.EnhetClient
import no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt.acl.NavEnhetProblemDto
import no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt.acl.NavEnhetResultDto
import no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt.acl.NavEnhetResultMapper.fromDto
import no.nav.pensjon.kalkulator.common.client.ExternalServiceClient
import no.nav.pensjon.kalkulator.person.FoedselsnummerUtil.redact
import no.nav.pensjon.kalkulator.tech.cache.CacheConfigurator.createCache
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import tools.jackson.core.JacksonException
import tools.jackson.databind.json.JsonMapper

@Component
class NavAnsattClient(
    @Value($$"${nav-ansatt.url}") baseUrl: String,
    @Value($$"${web-client.retry-attempts}") retryAttempts: String,
    webClientBuilder: WebClient.Builder,
    cacheManager: CaffeineCacheManager,
    private val jsonMapper: JsonMapper,
    private val traceAid: TraceAid,
) : ExternalServiceClient(retryAttempts), EnhetClient {

    private val log = KotlinLogging.logger {}
    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    private val cache: Cache<String, AnsattEnhetResult> =
        createCache("enhet", cacheManager)

    override fun fetchTjenestekontorEnhetListe(ansattId: String): AnsattEnhetResult =
        cache.getIfPresent(ansattId) ?: fetchFreshData(ansattId).also { cache.put(ansattId, it) }

    override fun service() = service

    private fun fetchFreshData(ansattId: String): AnsattEnhetResult {
        val uri = "$BASE_PATH/$ansattId/enheter"

        return try {
            webClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(::setHeaders)
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<NavEnhetResultDto>>() {})
                .retryWhen(retryBackoffSpec(uri))
                .block()
                ?.let(::fromDto)
                ?: AnsattEnhetResult(enhetListe = emptyList())
        } catch (e: WebClientRequestException) {
            throw EgressException("Failed calling $uri", e)
        } catch (e: WebClientResponseException) {
            throw EgressException(e.responseBodyAsString, e)
        } catch (e: EgressException) {
            handle(e)
        }
    }

    override fun toString(e: EgressException, uri: String) = "Failed calling $uri"

    private fun setHeaders(headers: HttpHeaders) {
        headers.setBearerAuth(EgressAccess.token(service).value)
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
    }

    private fun handle(egressException: EgressException): AnsattEnhetResult =
        try {
            jsonMapper.readValue(
                egressException.message,
                NavEnhetProblemDto::class.java
            )?.let(::fromDto) ?: throw egressException
        } catch (jacksonException: JacksonException) {
            log.warn(jacksonException) {
                redact(
                    "Failed to handle response from ${service.description}" +
                            " - ${jacksonException.message}" +
                            " - attempted to deserialize '${egressException.message}' as NavEnhetProblemDto"
                )
            }
            throw egressException
        }

    private companion object {
        private const val BASE_PATH = "/navansatt"

        private val service = EgressService.NAV_ANSATT
    }
}
