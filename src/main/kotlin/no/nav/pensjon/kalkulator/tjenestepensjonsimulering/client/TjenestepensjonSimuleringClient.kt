package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OFTPSimuleringsresultat

interface TjenestepensjonSimuleringClient {
    fun hentTjenestepensjonSimulering(request: SimuleringOFTPSpec): OFTPSimuleringsresultat
}