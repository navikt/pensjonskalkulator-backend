package no.nav.pensjon.kalkulator.opptjening.behandlet

import no.nav.pensjon.kalkulator.opptjening.behandlet.client.BeholdningOgMerknadClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class BeholdningOgMerknadService(
    private val client: BeholdningOgMerknadClient,
    private val pidGetter: PidGetter
) {
    fun beholdningOgMerknader(): AarvisBeholdningOgMerknader =
        client.fetchBeholdningOgMerknader(pidGetter.pid())
}