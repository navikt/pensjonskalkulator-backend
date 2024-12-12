package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpecV2

interface TjenestepensjonSimuleringClient {
    fun hentTjenestepensjonSimulering(
        request: SimuleringOffentligTjenestepensjonSpec,
        pid: Pid
    ): OffentligTjenestepensjonSimuleringsresultat

    fun hentTjenestepensjonSimulering(
        request: SimuleringOffentligTjenestepensjonSpecV2,
        pid: Pid
    ): OffentligTjenestepensjonSimuleringsresultat
}