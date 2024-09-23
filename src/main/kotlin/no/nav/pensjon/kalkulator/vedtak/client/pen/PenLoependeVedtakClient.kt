package no.nav.pensjon.kalkulator.vedtak.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.reactive.function.client.WebClient

class PenLoependeVedtakClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), LoependeVedtakClient {

    override fun hentLoependeVedtak(pid: String): LoependeVedtak {
        TODO("Not yet implemented")
    }

    private companion object {
        private const val PATH = "simulering/v1/tidligst-mulig-uttak"
    }
}