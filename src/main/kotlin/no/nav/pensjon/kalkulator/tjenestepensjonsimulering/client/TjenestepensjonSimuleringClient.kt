package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.SimuleringOffentligTjenestepensjonSpec

interface TjenestepensjonSimuleringClient {
    fun hentTjenestepensjonSimulering(
        request: SimuleringOffentligTjenestepensjonSpec,
        pid: Pid
    ): OffentligTjenestepensjonSimuleringsresultat
}
