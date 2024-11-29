package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OFTPSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOFTPSpec

interface TjenestepensjonSimuleringClient {
    fun hentTjenestepensjonSimulering(request: SimuleringOFTPSpec, pid: Pid): OFTPSimuleringsresultat
}