package no.nav.pensjon.kalkulator.sak

import no.nav.pensjon.kalkulator.sak.client.SakClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class SakService(
    private val sakClient: SakClient,
    private val pidGetter: PidGetter
) {
    fun harRelevantSak(): Boolean =
        sakClient.fetchSaker(pidGetter.pid())
            .any { it.type.relevant && it.status.relevant }

    suspend fun harSakType(sakType: SakType): Boolean =
        sakClient.fetchSakerAsync(pidGetter.pid())
            .any { it.type == sakType && it.status.relevant }

    fun sakStatus(): RelevantSakStatus =
        sakClient.fetchSaker(pidGetter.pid())
            .firstOrNull { it.type.relevant && it.status.relevant }
            ?.let { RelevantSakStatus(harSak = true, sakType = it.type) }
            ?: RelevantSakStatus(harSak = false, sakType = SakType.NONE)
}
