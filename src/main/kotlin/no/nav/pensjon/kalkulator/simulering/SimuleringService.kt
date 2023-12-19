package no.nav.pensjon.kalkulator.simulering

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class SimuleringService(
    private val simuleringClient: SimuleringClient,
    private val inntektService: InntektService,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    private val log = KotlinLogging.logger {}

    fun simulerAlderspensjon(impersonalSpec: ImpersonalSimuleringSpec): Simuleringsresultat {
        val pid = pidGetter.pid()

        val personalSpec = PersonalSimuleringSpec(
            pid,
            impersonalSpec.forventetInntekt ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact(),
            impersonalSpec.sivilstand ?: sivilstand(pid)
        )

        log.debug { "Simulerer med parametre $impersonalSpec og $personalSpec" }
        return simuleringClient.simulerAlderspensjon(impersonalSpec, personalSpec)
    }

    private fun sivilstand(pid: Pid) = personClient.fetchPerson(pid)?.sivilstand ?: Sivilstand.UOPPGITT
}
