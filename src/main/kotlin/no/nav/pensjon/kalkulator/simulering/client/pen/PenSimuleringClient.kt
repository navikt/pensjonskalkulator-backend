package no.nav.pensjon.kalkulator.simulering.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringEgressSpecDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenSimuleringResultDto
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenAnonymSimuleringSpecMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringMapper
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

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
            PenSimuleringMapper.toDto(impersonalSpec, personalSpec),
            SimuleringEgressSpecDto::class.java,
            PenSimuleringResultDto::class.java
        )?.let(PenSimuleringMapper::fromDto)
            ?: emptyResult()

    override fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec) =
        doPost(
            PATH,
            PenAnonymSimuleringSpecMapper.toDto(spec),
            PenAnonymSimuleringSpec::class.java,
            PenSimuleringResultDto::class.java
        )?.let(PenSimuleringMapper::fromDto)
            ?: emptyResult()

    private companion object {
        private const val PATH = "simulering/alderspensjon"

        private fun emptyResult() =
            Simuleringsresultat(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false)
            )
    }
}
