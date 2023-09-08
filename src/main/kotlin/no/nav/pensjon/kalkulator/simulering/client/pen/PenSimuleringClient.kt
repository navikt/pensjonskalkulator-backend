package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*
import no.nav.pensjon.kalkulator.simulering.client.pen.map.SimuleringMapper
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PenSimuleringClient(
    @Value("\${pen.url}") baseUrl: String,
    webClient: WebClient,
    callIdGenerator: CallIdGenerator,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClient, callIdGenerator, retryAttempts), SimuleringClient, Pingable {

    override fun simulerAlderspensjon(spec: SimuleringSpec) =
        doPost(
            PATH,
            SimuleringMapper.toDto(spec),
            SimuleringRequestDto::class.java,
            SimuleringResponseDto::class.java
        )?.let(SimuleringMapper::fromDto)
            ?: emptyResult()

    private companion object {
        private const val PATH = "simulering/alderspensjon"

        private fun emptyResult() = Simuleringsresultat(emptyList(), emptyList())
    }
}
