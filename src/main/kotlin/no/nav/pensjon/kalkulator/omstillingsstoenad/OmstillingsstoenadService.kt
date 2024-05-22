package no.nav.pensjon.kalkulator.omstillingsstoenad

import no.nav.pensjon.kalkulator.omstillingsstoenad.client.OmstillingsstoenadClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class OmstillingsstoenadService(
    private val client: OmstillingsstoenadClient,
    private val pidGetter: PidGetter,
    private val timeProvider: TimeProvider
) {

    fun mottarOmstillingsstoenad(): Boolean {
        return client.mottarOmstillingsstoenad(pidGetter.pid(), timeProvider.time().toLocalDate())
    }
}