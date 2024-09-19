package no.nav.pensjon.kalkulator.simulering

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SimuleringService(
    private val simuleringClient: SimuleringClient,
    private val inntektService: InntektService,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    private val log = KotlinLogging.logger {}

    fun simulerAlderspensjon(impersonalSpec: ImpersonalSimuleringSpec): SimuleringResult {
        val pid = pidGetter.pid()

        val personalSpec = PersonalSimuleringSpec(
            pid = pid,
            sivilstand = impersonalSpec.sivilstand ?: sivilstand(pid),
            aarligInntektFoerUttak = impersonalSpec.forventetAarligInntektFoerUttak
                ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()
        )

        log.debug { "Simulerer med parametre $impersonalSpec og $personalSpec" }
        checkAlder(pid.dato()) // NB: eksakt alder kan ikke alltid utledes fra fødselsnummer
        return simuleringClient.simulerAlderspensjon(impersonalSpec, personalSpec)
    }

    private fun sivilstand(pid: Pid) =
        personClient.fetchPerson(pid = pid, fetchFulltNavn = false)?.sivilstand ?: Sivilstand.UOPPGITT

    private fun checkAlder(foedselsdato: LocalDate) {
        foedselsdato.let {
            if (it < TIDLIGSTE_STOETTEDE_FOEDSELSDATO) {
                log.warn { "Simulerer med for tidlig fødselsdato - $it" }
            }
        }
    }

    private companion object {
        private val TIDLIGSTE_STOETTEDE_FOEDSELSDATO = LocalDate.of(1963, 1, 1)
    }
}
