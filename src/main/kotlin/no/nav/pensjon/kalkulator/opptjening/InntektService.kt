package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class InntektService(
    private val client: OpptjeningsgrunnlagClient,
    private val pidGetter: PidGetter
) {
    fun sistePensjonsgivendeInntekt(): Int {
        val grunnlag = client.fetchOpptjeningsgrunnlag(pidGetter.pid())
        return InntektUtil.sistePensjonsgivendeInntekt(grunnlag).intValueExact()
    }
}
