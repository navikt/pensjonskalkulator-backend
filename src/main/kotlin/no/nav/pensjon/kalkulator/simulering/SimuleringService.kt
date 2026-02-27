package no.nav.pensjon.kalkulator.simulering

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.stereotype.Service
import java.time.format.DateTimeParseException

@Service
class SimuleringService(
    private val simuleringClient: SimuleringClient,
    private val inntektService: InntektService,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    private val log = KotlinLogging.logger {}

    fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult =
        simuleringClient.simulerAnonymAlderspensjon(spec)

    fun simulerPersonligAlderspensjon(impersonalSpec: ImpersonalSimuleringSpec): SimuleringResult {
        val pid = pidGetter.pid()

        val personalSpec = PersonalSimuleringSpec(
            pid = pid,
            sivilstand = impersonalSpec.sivilstand ?: sivilstand(pid),
            aarligInntektFoerUttak = impersonalSpec.forventetAarligInntektFoerUttak
                ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()
        )

        log.debug { "Simulerer med parametre $impersonalSpec og $personalSpec" }
        return simuleringClient.simulerPersonligAlderspensjon(impersonalSpec, personalSpec)
    }

    /**
     * Same as simulerPersonligAlderspensjon but with improved handling of problems.
     */
    fun simulerPensjon(providedSpec: ImpersonalSimuleringSpec): SimuleringResult =
        try {
            val pid = pidGetter.pid()

            val registeredSpec = PersonalSimuleringSpec(
                pid = pid,
                sivilstand = providedSpec.sivilstand ?: sivilstand(pid),
                aarligInntektFoerUttak = providedSpec.forventetAarligInntektFoerUttak
                    ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()
            )

            log.debug { "Simulerer med parametre $providedSpec og $registeredSpec" }
            simuleringClient.simulerPersonligAlderspensjon(providedSpec, registeredSpec)
        } catch (e: BadRequestException) {
            problem(e, type = ProblemType.ANNEN_KLIENTFEIL)
        } catch (e: DateTimeParseException) {
            problem(e, type = ProblemType.ANNEN_KLIENTFEIL)
        } catch (e: EgressException) {
            problem(e, type = ProblemType.SERVERFEIL)
        }

    private fun sivilstand(pid: Pid) =
        personClient.fetchPerson(pid = pid, fetchFulltNavn = false)?.sivilstand ?: Sivilstand.UOPPGITT

    private companion object {
        private fun problem(e: RuntimeException, type: ProblemType) =
            SimuleringResult(
                alderspensjon = emptyList(),
                alderspensjonMaanedsbeloep = null,
                pre2025OffentligAfp = null,
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList(),
                problem = Problem(type, beskrivelse = e.message ?: "Ukjent feil - ${e.javaClass.simpleName}")
            )
    }
}
