package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class InntektService(
    private val client: OpptjeningsgrunnlagClient,
    private val pidGetter: PidGetter,
    private val timeProvider: TimeProvider
) {
    fun sistePensjonsgivendeInntekt(): Inntekt {
        val grunnlag = client.fetchOpptjeningsgrunnlag(pidGetter.pid())
        val sisteRelevanteOpptjeningAar = timeProvider.time().year - ANTALL_AAR_MELLOM_INNTEKT_TJENT_OG_SKATTELIGNET
        return InntektUtil.pensjonsgivendeInntekt(grunnlag, sisteRelevanteOpptjeningAar)
    }

    private companion object{
        private const val ANTALL_AAR_MELLOM_INNTEKT_TJENT_OG_SKATTELIGNET = 2
    }
}
