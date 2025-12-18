package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringOffentligTjenestepensjonSpec
interface TjenestepensjonSimuleringClient {
    fun hentTjenestepensjonSimulering(
        request: SimuleringOffentligTjenestepensjonSpec,
        pid: Pid
    ): OffentligTjenestepensjonSimuleringsresultat
}
