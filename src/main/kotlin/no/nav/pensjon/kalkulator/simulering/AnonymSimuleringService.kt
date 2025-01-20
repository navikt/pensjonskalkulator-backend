package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.simulering.client.AnonymSimuleringClient
import org.springframework.stereotype.Service

@Service
class AnonymSimuleringService(private val client: AnonymSimuleringClient) {

    fun simulerAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult =
        client.simulerAnonymAlderspensjon(spec)
}
