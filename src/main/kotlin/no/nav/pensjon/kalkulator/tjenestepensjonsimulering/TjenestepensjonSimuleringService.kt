package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOFTPSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OFTPSimuleringsresultat
import org.springframework.stereotype.Service

@Service
class TjenestepensjonSimuleringService(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringClient
) {

    fun hentTjenestepensjonSimulering(request: IngressSimuleringOFTPSpecV2): OFTPSimuleringsresultat {
        val pid = pidGetter.pid()
        val simuleringRequest = request.toSimuleringOFTPSpec(pid)
        return tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(simuleringRequest)
    }
}