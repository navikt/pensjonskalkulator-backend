package no.nav.pensjon.kalkulator.sak.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.sak.Sak
import no.nav.pensjon.kalkulator.sak.client.SakClient
import no.nav.pensjon.kalkulator.sak.client.pen.map.SakMapper
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenSakClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), SakClient {

    override fun fetchSaker(pid: Pid): List<Sak> =
        doGet(
            object : ParameterizedTypeReference<List<SakDto>>() {},
            FETCH_SAK_PATH,
            pid
        )?.let(SakMapper::fromDto)
            ?: emptyList()

    override suspend fun fetchSakerAsync(pid: Pid): List<Sak> =
        doGetAsync(
            object : ParameterizedTypeReference<List<SakDto>>() {},
            FETCH_SAK_PATH,
            pid
        )?.let(SakMapper::fromDto)
            ?: emptyList()

    override fun opprettAlderspensjonSak(pid: Pid): Sak =
        (doPost(
            path = OPPRETT_SAK_PATH,
            requestBody = "",
            requestClass = String::class.java,
            responseClass = SakDto::class.java,
            pid = pid,
            queryParams = mapOf("sakType" to PenSakType.ALDERSPENSJON.externalValue),
        ) ?: throw EgressException("No response body from opprett sak"))
            .let(SakMapper::fromDto)

    private companion object {
        private const val FETCH_SAK_PATH = "sak/sammendrag/v2"
        private const val OPPRETT_SAK_PATH = "sak"
    }
}
