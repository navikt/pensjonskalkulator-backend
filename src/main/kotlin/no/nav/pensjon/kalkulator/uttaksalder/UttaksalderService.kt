package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.opptjening.InntektUtil
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import org.springframework.stereotype.Service

@Service
class UttaksalderService(
    private val uttaksalderClient: UttaksalderClient,
    private val opptjeningsgrunnlagClient: OpptjeningsgrunnlagClient,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    fun finnTidligsteUttaksalder(specDto: UttaksalderIngressSpecDto): Alder? {
        val pid = pidGetter.pid()

        val uttaksalderSpec = UttaksalderSpec(
            pid = pid,
            sivilstand = specDto.sivilstand ?: sivilstand(pid),
            harEps = specDto.harEps ?: false,
            sisteInntekt = specDto.sisteInntekt ?: sistePensjonsgivendeInntekt(pid),
        )

        return uttaksalderClient.finnTidligsteUttaksalder(uttaksalderSpec).also(::updateMetric)
    }

    private fun sivilstand(pid: Pid) = personClient.fetchPerson(pid)?.sivilstand ?: Sivilstand.UOPPGITT

    private fun sistePensjonsgivendeInntekt(pid: Pid): Int {
        val grunnlag = opptjeningsgrunnlagClient.fetchOpptjeningsgrunnlag(pid)
        return InntektUtil.sistePensjonsgivendeInntekt(grunnlag).beloep.intValueExact()
    }

    private fun updateMetric(alder: Alder?) {
        val result = alder?.let { if (it.aar == 62 && it.maaneder == 1) "621" else it.aar.toString() } ?: "null"
        Metrics.countEvent("uttaksalder", result)
    }
}
