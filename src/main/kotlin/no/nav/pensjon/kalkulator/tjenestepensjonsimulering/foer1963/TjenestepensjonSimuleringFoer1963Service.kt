package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.TjenestepensjonSimuleringFoer1963Client
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TjenestepensjonSimuleringFoer1963Service(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringFoer1963Client
) {
    fun hentTjenestepensjonSimulering(spec: SimuleringOffentligTjenestepensjonFoer1963Spec): OffentligTjenestepensjonSimuleringFoer1963Resultat {
        return tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request = spec, pid = pidGetter.pid())
    }
}
