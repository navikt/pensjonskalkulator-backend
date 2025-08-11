package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import org.springframework.stereotype.Service

@Service
class TjenestepensjonSimuleringService(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringClient
) {
    fun hentTjenestepensjonSimulering(spec: SimuleringOffentligTjenestepensjonSpec): OffentligTjenestepensjonSimuleringsresultat =
        tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request = spec, pid = pidGetter.pid())
}
