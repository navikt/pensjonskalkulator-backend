package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import org.springframework.stereotype.Service

@Service
class AnonymSimuleringService(private val simuleringClient: SimuleringClient) {
    fun simulerAlderspensjon(spec: ImpersonalSimuleringSpec): Simuleringsresultat {
        return simuleringClient.simulerAnonymAlderspensjon(spec)
    }
}
