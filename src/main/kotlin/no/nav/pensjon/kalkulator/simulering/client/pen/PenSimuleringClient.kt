package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.pen.PenClient
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*
import no.nav.pensjon.kalkulator.simulering.client.pen.map.SimuleringMapper
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PenSimuleringClient(
    @Value("\${pen.url}") baseUrl: String,
    webClient: WebClient
) : PenClient(baseUrl, webClient), SimuleringClient, Pingable {
    override fun simulerAlderspensjon(spec: SimuleringSpec): Simuleringsresultat {
        val response = doPost(
            PATH,
            SimuleringMapper.toDto(spec),
            SimuleringRequestDto::class.java,
            SimuleringResponseDto::class.java,
        )

        return SimuleringMapper.fromDto(response ?: emptyDto())
    }

    companion object {
        private const val PATH = "$BASE_PATH/simulering/alderspensjon"

        private fun emptyDto() = SimuleringResponseDto(emptyList())
    }
}
