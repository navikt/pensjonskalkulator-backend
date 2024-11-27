package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOFTPSpecV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OFTPSimuleringsresultat

interface TjenestepensjonSimuleringClient {
    fun hentTjenestepensjonSimulering(request: IngressSimuleringOFTPSpecV1): OFTPSimuleringsresultat
}