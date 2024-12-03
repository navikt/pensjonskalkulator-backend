package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpec
import org.springframework.stereotype.Service

@Service
class TjenestepensjonSimuleringService(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringClient
) {

    fun hentTjenestepensjonSimulering(request: SimuleringOffentligTjenestepensjonSpec): OffentligTjenestepensjonSimuleringsresultat {
        val pid = pidGetter.pid()
        return tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request, pid)
    }
}