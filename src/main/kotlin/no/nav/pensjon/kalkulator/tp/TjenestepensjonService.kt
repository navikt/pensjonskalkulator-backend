package no.nav.pensjon.kalkulator.tp

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tp.client.TjenestepensjonClient
import org.springframework.stereotype.Service

@Service
class TjenestepensjonService(
    private val tjenestepensjonClient: TjenestepensjonClient,
    private val pidGetter: PidGetter
) {
    fun harTjenestepensjonsforhold() = tjenestepensjonClient.harTjenestepensjonsforhold(pidGetter.pid())
}
