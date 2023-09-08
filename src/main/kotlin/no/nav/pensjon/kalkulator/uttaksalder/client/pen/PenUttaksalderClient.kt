package no.nav.pensjon.kalkulator.uttaksalder.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderRequestDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderResponseDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.map.UttaksalderMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenUttaksalderClient(
    @Value("\${pen.url}") baseUrl: String,
    webClient: WebClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClient, callIdGenerator, retryAttempts), UttaksalderClient {
    override fun finnTidligsteUttaksalder(spec: UttaksalderSpec) =
        doPost(
            PATH,
            UttaksalderMapper.toDto(spec),
            UttaksalderRequestDto::class.java,
            UttaksalderResponseDto::class.java,
        )?.let(UttaksalderMapper::fromDto)

    private companion object {
        private const val PATH = "uttaksalder"
    }
}
