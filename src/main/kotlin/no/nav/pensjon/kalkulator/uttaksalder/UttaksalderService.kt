package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.opptjening.InntektUtil
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import org.springframework.stereotype.Service

@Service
class UttaksalderService(
    private val uttaksalderClient: UttaksalderClient,
    private val opptjeningsgrunnlagClient: OpptjeningsgrunnlagClient,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    fun finnTidligsteUttaksalder(specDto: UttaksalderSpecDto): Uttaksalder? {
        val pid = pidGetter.pid()

        val uttaksalderSpec = UttaksalderSpec(
            pid = pid,
            sivilstand = specDto.sivilstand ?: sivilstand(pid),
            harEps = specDto.harEps ?: false,
            sisteInntekt = specDto.sisteInntekt ?: sistePensjonsgivendeInntekt(pid),
        )

        return uttaksalderClient.finnTidligsteUttaksalder(uttaksalderSpec)
    }

    private fun sivilstand(pid: Pid) = personClient.fetchPerson(pid)?.sivilstand ?: Sivilstand.UOPPGITT

    private fun sistePensjonsgivendeInntekt(pid: Pid): Int {
        val grunnlag = opptjeningsgrunnlagClient.getOpptjeningsgrunnlag(pid)
        return InntektUtil.sistePensjonsgivendeInntekt(grunnlag).intValueExact()
    }
}
