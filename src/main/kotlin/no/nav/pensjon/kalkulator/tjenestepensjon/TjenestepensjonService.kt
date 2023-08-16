package no.nav.pensjon.kalkulator.tjenestepensjon

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.time.DateProvider
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import org.springframework.stereotype.Service

@Service
class TjenestepensjonService(
    private val tjenestepensjonClient: TjenestepensjonClient,
    private val pidGetter: PidGetter,
    private val dateProvider: DateProvider
) {
    fun harTjenestepensjonsforhold() =
        tjenestepensjonClient.harTjenestepensjonsforhold(pidGetter.pid(), dateProvider.now())
}
