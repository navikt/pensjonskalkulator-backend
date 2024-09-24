package no.nav.pensjon.kalkulator.vedtak.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.map.LoependeVedtakMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenLoependeVedtakClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), LoependeVedtakClient {

    override fun hentLoependeVedtak(pid: Pid): LoependeVedtak {
        return webClient
            .get()
            .uri(PATH)
            .header("fnr", pid.value)
            .retrieve()
            .bodyToMono(PenLoependeVedtakDto::class.java)
            .block()
            ?.let(LoependeVedtakMapper::fromDto)
            ?: throw EgressException("Kunne ikke hente loepende vedtak for brukeren")
    }

    private companion object {
        private const val PATH = "/api/simulering/vedtak/loependevedtak"
    }
}