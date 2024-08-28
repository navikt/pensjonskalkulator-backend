package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import org.springframework.stereotype.Service

@Service
class AnonymSimuleringService(private val simuleringClient: SimuleringClient) {
    fun simulerAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult {
        return simuleringClient.simulerAnonymAlderspensjon(spec)
    }
}
