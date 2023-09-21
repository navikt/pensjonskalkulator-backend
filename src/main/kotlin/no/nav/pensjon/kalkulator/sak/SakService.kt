package no.nav.pensjon.kalkulator.sak

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class SakService(
    private val sakClient: SakClient,
    private val pidGetter: PidGetter
) {
    fun harRelevantSak() =
        sakClient.fetchSaker(pidGetter.pid())
            .any { it.type.relevant && it.status.relevant }
}
