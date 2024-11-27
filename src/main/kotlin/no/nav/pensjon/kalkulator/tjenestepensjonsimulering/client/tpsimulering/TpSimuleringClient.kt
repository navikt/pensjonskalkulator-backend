package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOFTPSpecV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import org.springframework.stereotype.Component

@Component
class TpSimuleringClient : TjenestepensjonSimuleringClient {

    override fun hentTjenestepensjonSimulering(request: IngressSimuleringOFTPSpecV1): OFTPSimuleringsresultat {
        TODO("Not yet implemented")
    }
}