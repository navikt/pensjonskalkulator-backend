package no.nav.pensjon.kalkulator.lagring

import no.nav.pensjon.kalkulator.lagring.client.LagreSimuleringClient
import no.nav.pensjon.kalkulator.sak.SakService
import org.springframework.stereotype.Service

@Service
class LagreSimuleringService(
    private val sakService: SakService,
    private val client: LagreSimuleringClient
) {
    fun lagreSimulering(simulering: LagreSimulering): LagreSimuleringResponse {
        val sakId = sakService.hentEllerOpprettAlderspensjonSak()
        return client.lagreSimulering(sakId, simulering)
    }
}
