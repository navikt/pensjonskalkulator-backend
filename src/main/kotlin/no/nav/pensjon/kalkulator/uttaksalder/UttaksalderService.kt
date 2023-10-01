package no.nav.pensjon.kalkulator.uttaksalder

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.opptjening.InntektUtil
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.SimuleringType
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
    private val log = KotlinLogging.logger {}

    fun finnTidligsteUttaksalder(specDto: UttaksalderIngressSpecDto): Alder? {
        val pid = pidGetter.pid()

        val uttaksalderSpec = UttaksalderSpec(
            pid = pid,
            sivilstand = specDto.sivilstand ?: sivilstand(pid),
            harEps = specDto.harEps ?: false,
            sisteInntekt = specDto.sisteInntekt ?: sistePensjonsgivendeInntekt(pid),
            simuleringstype = specDto.simuleringstype ?: SimuleringType.ALDERSPENSJON,
        )

        log.info { "Finner første mulige uttaksalder med parametre $uttaksalderSpec" }
        return uttaksalderClient.finnTidligsteUttaksalder(uttaksalderSpec).also(::updateMetric)
    }

    private fun sivilstand(pid: Pid) = personClient.fetchPerson(pid)?.sivilstand ?: Sivilstand.UOPPGITT

    private fun sistePensjonsgivendeInntekt(pid: Pid): Int {
        val grunnlag = opptjeningsgrunnlagClient.fetchOpptjeningsgrunnlag(pid)
        return InntektUtil.sistePensjonsgivendeInntekt(grunnlag).beloep.intValueExact()
    }

    private companion object {
        private val teoretiskLavesteUttaksalder = Alder(62, 0)

        private fun updateMetric(alder: Alder?) {
            val maaneder = alder?.let {
                if (teoretiskLavesteUttaksalder == it) it.maaneder.toString() else "x"
            }

            val result = maaneder?.let { "${alder.aar}/$maaneder" } ?: "null"
            Metrics.countEvent("uttaksalder", result)
        }
    }
}
