package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PenPingClient(
    @Value("\${pen.url}") baseUrl: String,
    webClient: WebClient,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : PenClient(baseUrl, webClient, traceAid, retryAttempts)