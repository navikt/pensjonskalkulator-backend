package no.nav.pensjon.kalkulator.uttaksalder.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderEgressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.map.PenUttaksalderMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenUttaksalderClient(
    @Value("\${pen.url}") baseUrl: String,
    webClient: WebClient,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClient, traceAid, retryAttempts), UttaksalderClient {
    override fun finnTidligsteUttaksalder(spec: UttaksalderSpec) =
        doPost(
            PATH,
            PenUttaksalderMapper.toDto(spec),
            UttaksalderEgressSpecDto::class.java,
            UttaksalderDto::class.java,
        )?.let(PenUttaksalderMapper::fromDto)

    private companion object {
        private const val PATH = "uttaksalder"
    }
}
