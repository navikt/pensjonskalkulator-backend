package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class OpptjeningService(
    private val client: PensjonspoengClient,
    private val pidGetter: PidGetter
) {
    fun opptjening(): List<AarligOpptjening> =
        client.fetchPensjonspoeng(pidGetter.pid())
}