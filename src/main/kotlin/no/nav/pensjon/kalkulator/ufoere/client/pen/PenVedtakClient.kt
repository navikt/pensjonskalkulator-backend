package no.nav.pensjon.kalkulator.ufoere.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.ufoere.Vedtak
import no.nav.pensjon.kalkulator.ufoere.client.VedtakClient
import no.nav.pensjon.kalkulator.ufoere.client.pen.map.VedtakMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

@Component
class PenVedtakClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), VedtakClient {

    override fun bestemGjeldendeVedtak(
        pid: Pid,
        @DateTimeFormat(pattern = "yyyy-MM-dd") fom: LocalDate
    ): List<Vedtak> =
        doGet(
            object : ParameterizedTypeReference<List<VedtakDto>>() {},
            "$PATH$fom",
            pid
        )?.let(VedtakMapper::fromDto)
            ?: emptyList()

    private companion object {
        private const val PATH = "vedtak/bestemgjeldende?fom="
    }
}
