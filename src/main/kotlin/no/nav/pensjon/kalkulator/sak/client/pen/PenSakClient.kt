package no.nav.pensjon.kalkulator.sak.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import no.nav.pensjon.kalkulator.sak.Sak
import no.nav.pensjon.kalkulator.sak.SakClient
import no.nav.pensjon.kalkulator.sak.client.pen.map.SakMapper
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PenSakClient(
    @Value("\${pen.url}") private val baseUrl: String,
    webClient: WebClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClient, callIdGenerator, retryAttempts), SakClient {
    override fun fetchSaker(pid: Pid): List<Sak> =
        doGet(
            object : ParameterizedTypeReference<List<SakDto>>() {},
            PATH,
            pid
        )?.let(SakMapper::fromDto)
            ?: emptyList()

    private companion object {
        private const val PATH = "sak/sammendrag"
    }
}
