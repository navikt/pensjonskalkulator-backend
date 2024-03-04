package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenPingClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts)
