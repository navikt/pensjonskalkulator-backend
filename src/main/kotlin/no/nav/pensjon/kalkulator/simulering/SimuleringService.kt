package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.opptjening.InntektUtil
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.foersteUttaksdato
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.alder
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class SimuleringService(
    private val simuleringClient: SimuleringClient,
    private val opptjeningsgrunnlagClient: OpptjeningsgrunnlagClient,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    fun simulerAlderspensjon(specDto: SimuleringSpecDto): Simuleringsresultat {
        val pid = pidGetter.pid()

        val simuleringSpec = SimuleringSpec(
            simuleringstype = specDto.simuleringstype,
            pid = pid,
            forventetInntekt = specDto.forventetInntekt ?: sistePensjonsgivendeInntekt(pid),
            uttaksgrad = specDto.uttaksgrad,
            foersteUttaksdato = foersteUttaksdato(specDto.foedselsdato, alder(specDto.foersteUttaksalder)),
            sivilstand = specDto.sivilstand ?: sivilstand(pid),
            epsHarInntektOver2G = specDto.epsHarInntektOver2G
        )

        return simuleringClient.simulerAlderspensjon(simuleringSpec)
    }

    private fun sivilstand(pid: Pid) = personClient.fetchPerson(pid)?.sivilstand ?: Sivilstand.UOPPGITT

    private fun sistePensjonsgivendeInntekt(pid: Pid): Int {
        val grunnlag = opptjeningsgrunnlagClient.fetchOpptjeningsgrunnlag(pid)
        return InntektUtil.sistePensjonsgivendeInntekt(grunnlag).beloep.intValueExact()
    }
}
