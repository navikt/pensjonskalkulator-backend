package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*
import no.nav.pensjon.kalkulator.simulering.client.pen.map.SimuleringMapper
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PenSimuleringClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), SimuleringClient, Pingable {

    override fun simulerAlderspensjon(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        doPost(
            PATH,
            SimuleringMapper.toDto(impersonalSpec, personalSpec),
            SimuleringRequestDto::class.java,
            SimuleringResponseDto::class.java
        )?.let(SimuleringMapper::fromDto)
            ?: emptyResult()

    private companion object {
        private const val PATH = "simulering/alderspensjon"

        private fun emptyResult() = Simuleringsresultat(emptyList(), emptyList())
    }
}
