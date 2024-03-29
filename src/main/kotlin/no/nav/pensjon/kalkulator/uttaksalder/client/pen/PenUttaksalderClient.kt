package no.nav.pensjon.kalkulator.uttaksalder.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
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
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), UttaksalderClient {

    override fun finnTidligsteUttaksalder(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec
    ) =
        doPost(
            PATH,
            PenUttaksalderMapper.toDto(impersonalSpec, personalSpec),
            UttaksalderEgressSpecDto::class.java,
            UttaksalderDto::class.java,
        )?.let(PenUttaksalderMapper::fromDto)

    private companion object {
        private const val PATH = "uttaksalder"
    }
}
