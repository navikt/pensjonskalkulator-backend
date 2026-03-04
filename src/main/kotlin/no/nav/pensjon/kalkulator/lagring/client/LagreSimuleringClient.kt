package no.nav.pensjon.kalkulator.lagring.client

import no.nav.pensjon.kalkulator.lagring.LagreSimulering

interface LagreSimuleringClient {
    fun lagreSimulering(lagreSimulering: LagreSimulering)
}
