package no.nav.pensjon.kalkulator.simulering.client

import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat

interface SimuleringClient {

    fun simulerAlderspensjon(spec: SimuleringSpec): Simuleringsresultat
}
