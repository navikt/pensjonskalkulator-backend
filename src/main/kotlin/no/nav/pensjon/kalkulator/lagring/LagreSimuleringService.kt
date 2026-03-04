package no.nav.pensjon.kalkulator.lagring

import no.nav.pensjon.kalkulator.lagring.client.LagreSimuleringClient
import org.springframework.stereotype.Service

@Service
class LagreSimuleringService(
    private val client: LagreSimuleringClient
) {
    fun lagreSimulering(result: LagreSimulering) {
        client.lagreSimulering(result)
    }
}
