package no.nav.pensjon.kalkulator.lagring.client

import no.nav.pensjon.kalkulator.lagring.LagreSimulering
import no.nav.pensjon.kalkulator.lagring.LagreSimuleringResponse

interface LagreSimuleringClient {
    fun lagreSimulering(sakId: Long, lagreSimulering: LagreSimulering): LagreSimuleringResponse
}
